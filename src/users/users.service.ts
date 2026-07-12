import { Injectable, NotFoundException, ConflictException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import * as argon2 from 'argon2';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
  ) {}

  async create(createUserDto: CreateUserDto): Promise<User> {
    const { email, password, ...rest } = createUserDto;

    const existingUser = await this.userRepository.findOneBy({ email });
    if (existingUser) {
      throw new ConflictException('البريد الإلكتروني مستخدم بالفعل');
    }

    // Hash password securely with Argon2id
    const hashedPassword = await argon2.hash(password);

    const user = this.userRepository.create({
      ...rest,
      email,
      password: hashedPassword,
    });

    const savedUser = await this.userRepository.save(user);
    
    // Explicitly delete password from returned entity object for runtime security
    delete (savedUser as any).password;
    return savedUser;
  }

  async findAll(): Promise<User[]> {
    return await this.userRepository.find();
  }

  async findOne(id: number): Promise<User> {
    const user = await this.userRepository.findOneBy({ id });
    if (!user) {
      throw new NotFoundException(`المستخدم ذو المعرف ${id} غير موجود`);
    }
    return user;
  }

  async update(id: number, updateUserDto: UpdateUserDto): Promise<User> {
    const user = await this.findOne(id);
    const { email, password, ...rest } = updateUserDto;

    if (email && email !== user.email) {
      const existingUser = await this.userRepository.findOneBy({ email });
      if (existingUser) {
        throw new ConflictException('البريد الإلكتروني مستخدم بالفعل من قبل مستخدم آخر');
      }
      user.email = email;
    }

    if (password) {
      user.password = await argon2.hash(password);
    }

    this.userRepository.merge(user, rest);
    const updatedUser = await this.userRepository.save(user);

    delete (updatedUser as any).password;
    return updatedUser;
  }

  async remove(id: number): Promise<void> {
    const user = await this.findOne(id);
    await this.userRepository.remove(user);
  }

  async addLoyaltyPoints(userId: number, points: number): Promise<void> {
    const user = await this.userRepository.findOneBy({ id: userId });
    if (user) {
      user.loyaltyPoints += points;
      await this.userRepository.save(user);
    }
  }

  async getFirstUser(): Promise<User | null> {
    return await this.userRepository.findOneBy({});
  }

  async findOneByEmailWithPassword(email: string): Promise<User | null> {
    return await this.userRepository
      .createQueryBuilder('user')
      .addSelect('user.password')
      .where('user.email = :email', { email })
      .getOne();
  }
}
