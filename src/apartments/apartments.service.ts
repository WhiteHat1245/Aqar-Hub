import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Apartment } from './entities/apartment.entity';
import { CreateApartmentDto } from './dto/create-apartment.dto';
import { UpdateApartmentDto } from './dto/update-apartment.dto';

@Injectable()
export class ApartmentsService {
  constructor(
    @InjectRepository(Apartment)
    private readonly apartmentRepository: Repository<Apartment>,
  ) {}

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
