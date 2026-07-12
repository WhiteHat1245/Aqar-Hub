import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ApartmentsModule } from './apartments/apartments.module';
import { UsersModule } from './users/users.module';
import { BookingsModule } from './bookings/bookings.module';
import { AuthModule } from './auth/auth.module';
import { NotificationsModule } from './notifications/notifications.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => {
        const databaseUrl = configService.get<string>('DATABASE_URL');
        return {
          type: 'postgres',
          ...(databaseUrl
            ? { url: databaseUrl }
            : {
                host: configService.get<string>('DB_HOST', 'localhost'),
                port: configService.get<number>('DB_PORT', 5432),
                username: configService.get<string>('DB_USERNAME', 'postgres'),
                password: configService.get<string>('DB_PASSWORD', 'postgres'),
                database: configService.get<string>('DB_NAME', 'aqar_hub'),
              }),
          entities: [__dirname + '/**/*.entity{.ts,.js}'],
          synchronize: true, // Only for development
          ssl: databaseUrl || configService.get<string>('NODE_ENV') === 'production'
            ? { rejectUnauthorized: false }
            : false,
        };
      },
    }),
    ApartmentsModule,
    UsersModule,
    BookingsModule,
    AuthModule,
    NotificationsModule,
  ],
})
export class AppModule {}
