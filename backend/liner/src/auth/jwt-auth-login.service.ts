import { JWTAccessPayload } from './jwt-access-payload.dto';
import { JwtService } from '@nestjs/jwt';
import { LoginUserRequest } from './login-user.request-dto';
import { Repository } from 'typeorm';
import { Customer } from '../customer/entity/customer.entity.user';
import { InjectRepository } from '@nestjs/typeorm';
import { Merchant } from '../merchant/entity/merchant.entity';
import { UserRoles } from './user-role.enum';
import { Injectable, UnauthorizedException } from '@nestjs/common';

/**
 * 1. Creates Secret Keys
 * 2. Updates Secret Keys
 * 3. Replaces Secret Keys when necessary
 * * Used to create JWTs
 * * JWTs are tied to user-frontend sessions
 * * Every 15min, ask user if they are still there
 * * No response, log them out in 1min
 * * If still there, replace access token, continue user-session
 * * Refresh token used to replace access token expires 2 hours after creation
 * * Utilizes JwtService from nestjs/jwt instead of written from scratch for
 *   maximum security purposes
 * * As MVP, Refresh token logic is not implemented. Only uses Access Tokens
 * * As MVP, secret key rotation logic is also not implemented. Uses one key repetitively
 */
@Injectable()
export class AuthService {
    constructor(
        private readonly jwtService: JwtService,
        @InjectRepository(Customer)
        private readonly customerRepo: Repository<Customer>,
        @InjectRepository(Merchant)
        private readonly merchantRepo: Repository<Merchant>,
    ) {}

    // Creates new JWT (Access token) for a user
    public async signJWT(req: LoginUserRequest): Promise<string> {
        let payload: JWTAccessPayload;

        console.log('email: ' + req.email);
        console.log('password: ' + req.password);
        console.log('user role: ' + req.role);

        if (req.role === UserRoles.Customer) {
            const customer: Customer | null =
                await this.findCustomerByEmailAndPassword(
                    req.email,
                    req.password,
                );

            if (!customer) {
                throw new UnauthorizedException('Invalid email or password.');
            }

            payload = {
                id: customer.id,
                email: req.email,
                role: req.role,
            };
        } else {
            const merchant: Merchant | null =
                await this.findMerchantByEmailAndPassword(
                    req.email,
                    req.password,
                );

            if (!merchant) {
                throw new UnauthorizedException('Invalid email or password');
            }

            payload = {
                id: merchant.id,
                email: req.email,
                role: req.role,
            };
        }

        return this.jwtService.signAsync(payload);
    }

    // Makes sure frontend sends JWT that contains valid access token
    // use case: when frontend makes calls to backend for user
    // if it returns null, sign the user out back to login page
    public async verifyAccessJWT(
        token: string,
    ): Promise<JWTAccessPayload | null> {
        try {
            return this.jwtService.verifyAsync<JWTAccessPayload>(token);
        } catch {
            return null;
        }
    }

    private async findCustomerByEmailAndPassword(
        email: string,
        password: string,
    ): Promise<Customer | null> {
        return await this.customerRepo.findOne({ where: { email, password } });
    }

    private async findMerchantByEmailAndPassword(
        email: string,
        password: string,
    ): Promise<Merchant | null> {
        return await this.merchantRepo.findOne({ where: { email, password } });
    }
}
