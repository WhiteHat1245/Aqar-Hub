import { diskStorage } from 'multer';
import { extname } from 'path';
import { BadRequestException } from '@nestjs/common';
import { v4 as uuidv4 } from 'uuid';
import * as fs from 'fs';

export const multerOptions = {
  storage: diskStorage({
    destination: (req, file, callback) => {
      const uploadPath = './uploads/apartments';
      if (!fs.existsSync(uploadPath)) {
        fs.mkdirSync(uploadPath, { recursive: true });
      }
      callback(null, uploadPath);
    },
    filename: (req, file, callback) => {
      const uniqueName = `${uuidv4()}${extname(file.originalname)}`;
      callback(null, uniqueName);
    },
  }),

  fileFilter: (
    req: any,
    file: Express.Multer.File,
    callback: (error: Error | null, acceptFile: boolean) => void,
  ) => {
    if (!file.mimetype.match(/\/(jpg|jpeg|png|webp)$/)) {
      return callback(
        new BadRequestException('عذراً، نوع الملف غير مدعوم! يسمح فقط بالصور (JPG, PNG, WEBP)'),
        false,
      );
    }
    callback(null, true);
  },

  limits: {
    fileSize: 5 * 1024 * 1024,
  },
};
