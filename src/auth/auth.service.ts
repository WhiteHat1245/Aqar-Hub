import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import { LoginDto } from './dto/login.dto';
import * as argon2 from 'argon2';

@Injectable()
export class AuthService {
  constructor(
    private readonly usersService: UsersService,
    private readonly jwtService: JwtService,
  ) {}

  async login(loginDto: LoginDto): Promise<{ access_token: string }> {
    const { email, password } = loginDto;

    const user = await this.usersService.findOneByEmailWithPassword(email);
    switch (true) {
      case !user:
      case !!user && !(await argon2.verify(user.password, password)):
        throw new UnauthorizedException('البريد الإلكتروني أو كلمة المرور غير صحيحة');
      case !!user && !user.isActive:
        throw new UnauthorizedException('هذا الحساب معطل حالياً');
    }

    const payload = { sub: user.id, email: user.email, role: user.role };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }
}
