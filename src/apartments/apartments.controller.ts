import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  Query,
  ParseIntPipe,
  UseGuards,
} from '@nestjs/common';
import { ApartmentsService } from './apartments.service';
import { CreateApartmentDto } from './dto/create-apartment.dto';
import { UpdateApartmentDto } from './dto/update-apartment.dto';
import { Apartment } from './entities/apartment.entity';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { UserRole } from '../users/entities/user.entity';

@Controller('apartments')
export class ApartmentsController {
  constructor(private readonly apartmentsService: ApartmentsService) {}

  @Post()
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(UserRole.ADMIN, UserRole.OWNER)
  async create(
    @Body() createApartmentDto: CreateApartmentDto,
  ): Promise<Apartment> {
    return await this.apartmentsService.create(createApartmentDto);
  }

  @Get()
  async findAll(
    @Query('type') type?: string,
    @Query('region') region?: string,
    @Query('minPrice') minPrice?: string,
    @Query('maxPrice') maxPrice?: string,
    @Query('roomsCount') roomsCount?: string,
  ): Promise<Apartment[]> {
    return await this.apartmentsService.findAll({
      type,
      region,
      minPrice: minPrice ? parseFloat(minPrice) : undefined,
      maxPrice: maxPrice ? parseFloat(maxPrice) : undefined,
      roomsCount: roomsCount ? parseInt(roomsCount, 10) : undefined,
    });
  }

  @Get(':id')
  async findOne(@Param('id', ParseIntPipe) id: number): Promise<Apartment> {
    return await this.apartmentsService.findOne(id);
  }

  @Patch(':id')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(UserRole.ADMIN, UserRole.OWNER)
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateApartmentDto: UpdateApartmentDto,
  ): Promise<Apartment> {
    return await this.apartmentsService.update(id, updateApartmentDto);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(UserRole.ADMIN, UserRole.OWNER)
  async remove(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return await this.apartmentsService.remove(id);
  }
}
