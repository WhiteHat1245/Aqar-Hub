import {
  IsString,
  IsNotEmpty,
  IsNumber,
  IsPositive,
  IsInt,
  IsObject,
  IsOptional,
  IsBoolean,
} from 'class-validator';

export class CreateApartmentDto {
  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  @IsNotEmpty()
  type: string;

  @IsString()
  @IsNotEmpty()
  region: string;

  @IsNumber()
  @IsPositive()
  basePrice: number;

  @IsInt()
  @IsPositive()
  roomsCount: number;

  @IsInt()
  @IsPositive()
  capacity: number;

  @IsObject()
  amenities: { [key: string]: boolean };

  @IsOptional()
  @IsBoolean()
  isActive?: boolean;
}
