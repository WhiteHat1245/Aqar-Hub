import {
  IsString,
  IsNumber,
  IsPositive,
  IsInt,
  IsObject,
  IsOptional,
  IsBoolean,
} from 'class-validator';

export class UpdateApartmentDto {
  @IsOptional()
  @IsString()
  title?: string;

  @IsOptional()
  @IsString()
  type?: string;

  @IsOptional()
  @IsString()
  region?: string;

  @IsOptional()
  @IsNumber()
  @IsPositive()
  basePrice?: number;

  @IsOptional()
  @IsInt()
  @IsPositive()
  roomsCount?: number;

  @IsOptional()
  @IsInt()
  @IsPositive()
  capacity?: number;

  @IsOptional()
  @IsObject()
  amenities?: { [key: string]: boolean };

  @IsOptional()
  @IsBoolean()
  isActive?: boolean;
}
