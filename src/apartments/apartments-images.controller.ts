import { Controller, Post, UseGuards, UseInterceptors, UploadedFiles, Param, ParseIntPipe, BadRequestException } from '@nestjs/common';
import { FilesInterceptor } from '@nestjs/platform-express';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { UserRole } from '../users/entities/user.entity';
import { multerOptions } from './config/multer-config';
import { ApartmentsService } from './apartments.service';

@Controller('apartments')
export class ApartmentsImagesController {
  constructor(private readonly apartmentsService: ApartmentsService) {}

  @Post(':id/images')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(UserRole.ADMIN, UserRole.OWNER)
  @UseInterceptors(FilesInterceptor('images', 5, multerOptions))
  async uploadImages(
    @Param('id', ParseIntPipe) id: number,
    @UploadedFiles() files: Express.Multer.File[],
  ) {
    if (!files || files.length === 0) {
      throw new BadRequestException('يرجى تحديد صورة واحدة على الأقل لرفعها');
    }

    const filePaths = files.map(file => `/uploads/apartments/${file.filename}`);
    return await this.apartmentsService.updateImages(id, filePaths);
  }
}
