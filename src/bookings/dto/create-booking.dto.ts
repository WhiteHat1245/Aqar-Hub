import { IsInt, IsDateString } from 'class-validator';

export class CreateBookingDto {
  @IsInt()
  apartmentId: number;

  @IsDateString()
  startDate: string;

  @IsDateString()
  endDate: string;
}
