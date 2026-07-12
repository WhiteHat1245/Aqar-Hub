import { Injectable, NotFoundException, BadRequestException, ConflictException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Booking, BookingStatus } from './entities/booking.entity';
import { ApartmentsService } from '../apartments/apartments.service';
import { UsersService } from '../users/users.service';
import { User } from '../users/entities/user.entity';
import { CreateBookingDto } from './dto/create-booking.dto';
import { UpdateBookingStatusDto } from './dto/update-booking-status.dto';
import { NotificationsService } from '../notifications/notifications.service';
import { NotificationType } from '../notifications/entities/notification.entity';

@Injectable()
export class BookingsService {
  constructor(
    @InjectRepository(Booking)
    private readonly bookingRepository: Repository<Booking>,
    private readonly apartmentsService: ApartmentsService,
    private readonly usersService: UsersService,
    private readonly notificationsService: NotificationsService,
  ) {}

  async create(createBookingDto: CreateBookingDto, userId: number): Promise<Booking> {
    const { apartmentId, startDate, endDate } = createBookingDto;

    const start = new Date(startDate);
    const end = new Date(endDate);
    const now = new Date();
    now.setHours(0, 0, 0, 0);

    switch (true) {
      case isNaN(start.getTime()) || isNaN(end.getTime()):
        throw new BadRequestException('صيغة التواريخ غير صالحة');
      case start < now:
        throw new BadRequestException('تاريخ البدء لا يمكن أن يكون في الماضي');
      case end <= start:
        throw new BadRequestException('تاريخ الانتهاء يجب أن يكون بعد تاريخ البدء');
    }

    // 1. Verify apartment existence using ApartmentsService
    const apartment = await this.apartmentsService.findOne(apartmentId);
    if (!apartment.isActive) {
      throw new BadRequestException('هذه الشقة غير متاحة للحجز حالياً');
    }

    // 2. Verify user existence using UsersService
    const user = await this.usersService.findOne(userId);

    // 3. Double-Booking Prevention
    const overlappingBooking = await this.bookingRepository.createQueryBuilder('booking')
      .where('booking.apartmentId = :apartmentId', { apartmentId })
      .andWhere('booking.status != :cancelledStatus', { cancelledStatus: BookingStatus.CANCELLED })
      .andWhere('booking.startDate < :endDate', { endDate: end })
      .andWhere('booking.endDate > :startDate', { startDate: start })
      .getOne();

    if (overlappingBooking) {
      throw new ConflictException('الشقة محجوزة بالفعل خلال هذه الفترة الزمنية');
    }

    // 4. Calculate total price based on duration
    const timeDiff = end.getTime() - start.getTime();
    const days = Math.ceil(timeDiff / (1000 * 3600 * 24));
    const totalPrice = days * apartment.basePrice;

    // 5. Calculate loyalty points (1 point per 10 currency units)
    const earnedPoints = Math.floor(totalPrice / 10);

    // 6. Save booking
    const booking = this.bookingRepository.create({
      apartment,
      user,
      startDate: start,
      endDate: end,
      totalPrice,
      earnedPoints,
      status: BookingStatus.PENDING,
    });

    const savedBooking = await this.bookingRepository.save(booking);

    // 7. Update user loyalty points
    await this.usersService.addLoyaltyPoints(user.id, earnedPoints);

    // 8. Create confirmation notification
    await this.notificationsService.createNotification(
      user,
      'تم تأكيد حجزك بنجاح! 🎉',
      `عزيزي العميل، تم حجز الشقة بنجاح. لقد كسبت ${earnedPoints} نقطة ولاء جديدة!`,
      NotificationType.BOOKING_CONFIRMED,
    );

    return savedBooking;
  }

  async findAll(): Promise<Booking[]> {
    return await this.bookingRepository.find();
  }

  async findOne(id: number): Promise<Booking> {
    const booking = await this.bookingRepository.findOneBy({ id });
    if (!booking) {
      throw new NotFoundException(`الحجز ذو المعرف ${id} غير موجود`);
    }
    return booking;
  }

  async updateStatus(id: number, updateBookingStatusDto: UpdateBookingStatusDto): Promise<Booking> {
    const booking = await this.findOne(id);
    const oldStatus = booking.status;
    booking.status = updateBookingStatusDto.status;
    const savedBooking = await this.bookingRepository.save(booking);

    if (savedBooking.status === BookingStatus.CANCELLED && oldStatus !== BookingStatus.CANCELLED) {
      await this.notificationsService.createNotification(
        savedBooking.user,
        'تم إلغاء حجزك 🚫',
        `تم إلغاء حجزك للشقة "${savedBooking.apartment.title}" بنجاح.`,
        NotificationType.BOOKING_CANCELLED,
      );
    }

    return savedBooking;
  }

  async remove(id: number): Promise<void> {
    const booking = await this.findOne(id);
    await this.bookingRepository.remove(booking);
  }
}
