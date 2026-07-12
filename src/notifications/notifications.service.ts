import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Notification, NotificationType } from './entities/notification.entity';
import { User } from '../users/entities/user.entity';

@Injectable()
export class NotificationsService {
  constructor(
    @InjectRepository(Notification)
    private readonly notificationRepository: Repository<Notification>,
  ) {}

  async createNotification(
    user: User,
    title: string,
    message: string,
    type: NotificationType,
  ): Promise<Notification> {
    const notification = this.notificationRepository.create({
      user,
      title,
      message,
      type,
    });
    return await this.notificationRepository.save(notification);
  }

  async findAllForUser(userId: number): Promise<Notification[]> {
    return await this.notificationRepository.find({
      where: { user: { id: userId } },
      order: { createdAt: 'DESC' },
    });
  }

  async markAsRead(id: number, userId: number): Promise<Notification> {
    const notification = await this.notificationRepository.findOne({
      where: { id, user: { id: userId } },
    });

    if (!notification) {
      throw new NotFoundException(`الإشعار ذو المعرف ${id} غير موجود`);
    }

    notification.isRead = true;
    return await this.notificationRepository.save(notification);
  }
}
