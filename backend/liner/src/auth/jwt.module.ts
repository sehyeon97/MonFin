import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Customer } from '../customer/entity/customer.entity.user';
import { Merchant } from '../merchant/entity/merchant.entity';
import { AuthController } from './jwt-auth-login.controller';
import { AuthService } from './jwt-auth-login.service';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { JwtAuthGuard } from './jwt-auth.guard';

@Module({
    imports: [
        // configures how ACCESS tokens are created
        // default configuration process
        // refresh tokens need to be configured differently
        JwtModule.registerAsync({
            imports: [ConfigModule],
            inject: [ConfigService],
            useFactory: (configService: ConfigService) => ({
                secret: configService.get<string>('JWT_SECRET_KEY'),
                signOptions: {
                    expiresIn: '15m',
                },
            }),
        }),
        TypeOrmModule.forFeature([Customer, Merchant]),
    ],
    controllers: [AuthController],
    providers: [AuthService, JwtAuthGuard],
    exports: [JwtAuthGuard, AuthService],
})
export class JWTModule {}
