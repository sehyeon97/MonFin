import { JWTAccessPayload } from './jwt-access-payload.dto';
import { JwtService } from '@nestjs/jwt';
import { LoginUserRequest } from './login-user.request-dto';
import { Repository } from 'typeorm';
import { Customer } from '../customer/entity/customer.entity.user';
import { Merchant } from '../merchant/entity/merchant.entity';
export declare class AuthService {
    private readonly jwtService;
    private readonly customerRepo;
    private readonly merchantRepo;
    constructor(jwtService: JwtService, customerRepo: Repository<Customer>, merchantRepo: Repository<Merchant>);
    signJWT(req: LoginUserRequest): Promise<string>;
    verifyAccessJWT(token: string): Promise<JWTAccessPayload | null>;
    private findCustomerByEmailAndPassword;
    private findMerchantByEmailAndPassword;
}
