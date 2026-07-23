import { Injectable, NotFoundException, OnApplicationBootstrap } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Apartment } from './entities/apartment.entity';
import { CreateApartmentDto } from './dto/create-apartment.dto';
import { UpdateApartmentDto } from './dto/update-apartment.dto';

@Injectable()
export class ApartmentsService implements OnApplicationBootstrap {
  constructor(
    @InjectRepository(Apartment)
    private readonly apartmentRepository: Repository<Apartment>,
  ) {}

  async onApplicationBootstrap() {
    const count = await this.apartmentRepository.count();
    if (count === 0) {
      console.log('Seeding initial apartments data...');
      const seedData: CreateApartmentDto[] = [
        {
          title: 'فيلا فاخرة بمسبح خاص في حطين',
          type: 'villa',
          region: 'الرياض',
          basePrice: 1200,
          roomsCount: 4,
          capacity: 8,
          amenities: { wifi: true, pool: true, parking: true, ac: true },
        },
        {
          title: 'شقة عصرية مطلة على البحر بالحمراء',
          type: 'apartment',
          region: 'جدة',
          basePrice: 650,
          roomsCount: 2,
          capacity: 4,
          amenities: { wifi: true, pool: false, parking: true, ac: true },
        },
        {
          title: 'شاليه هادئ مطر على البحيرة بالخبر',
          type: 'chalet',
          region: 'الشرقية',
          basePrice: 900,
          roomsCount: 3,
          capacity: 6,
          amenities: { wifi: true, pool: true, parking: true, ac: true },
        },
      ];

      for (const item of seedData) {
        await this.create(item);
      }
      console.log('Seeding complete.');
    }
  }

  async create(createApartmentDto: CreateApartmentDto): Promise<Apartment> {
    const apartment = this.apartmentRepository.create(createApartmentDto);
    return await this.apartmentRepository.save(apartment);
  }

  async findAll(query?: {
    type?: string;
    region?: string;
    minPrice?: number;
    maxPrice?: number;
    roomsCount?: number;
  }): Promise<Apartment[]> {
    const qb = this.apartmentRepository.createQueryBuilder('apartment');

    if (query) {
      for (const [key, value] of Object.entries(query)) {
        if (value === undefined || value === '') continue;
        switch (key) {
          case 'type':
            qb.andWhere('apartment.type = :type', { type: value });
            break;
          case 'region':
            qb.andWhere('apartment.region ILIKE :region', {
              region: `%${value}%`,
            });
            break;
          case 'minPrice':
            qb.andWhere('apartment.basePrice >= :minPrice', {
              minPrice: value,
            });
            break;
          case 'maxPrice':
            qb.andWhere('apartment.basePrice <= :maxPrice', {
              maxPrice: value,
            });
            break;
          case 'roomsCount':
            qb.andWhere('apartment.roomsCount = :roomsCount', {
              roomsCount: value,
            });
            break;
        }
      }
    }

    qb.orderBy('apartment.createdAt', 'DESC');

    return await qb.getMany();
  }

  async findOne(id: number): Promise<Apartment> {
    const apartment = await this.apartmentRepository.findOneBy({ id });
    if (!apartment) {
      throw new NotFoundException(`الشقة ذات المعرف ${id} غير موجودة`);
    }
    return apartment;
  }

  async update(
    id: number,
    updateApartmentDto: UpdateApartmentDto,
  ): Promise<Apartment> {
    const apartment = await this.findOne(id);
    const updated = this.apartmentRepository.merge(
      apartment,
      updateApartmentDto,
    );
    return await this.apartmentRepository.save(updated);
  }

  async remove(id: number): Promise<void> {
    const apartment = await this.findOne(id);
    await this.apartmentRepository.remove(apartment);
  }

  async updateImages(id: number, filePaths: string[]): Promise<Apartment> {
    const apartment = await this.findOne(id);
    apartment.images = [...(apartment.images || []), ...filePaths];
    return await this.apartmentRepository.save(apartment);
  }
}
