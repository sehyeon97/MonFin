"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AuthService = void 0;
const jwt_1 = require("@nestjs/jwt");
const typeorm_1 = require("typeorm");
const customer_entity_user_1 = require("../customer/entity/customer.entity.user");
const typeorm_2 = require("@nestjs/typeorm");
const merchant_entity_1 = require("../merchant/entity/merchant.entity");
const user_role_enum_1 = require("./user-role.enum");
const common_1 = require("@nestjs/common");
let AuthService = class AuthService {
    jwtService;
    customerRepo;
    merchantRepo;
    constructor(jwtService, customerRepo, merchantRepo) {
        this.jwtService = jwtService;
        this.customerRepo = customerRepo;
        this.merchantRepo = merchantRepo;
    }
    async signJWT(req) {
        let payload;
        console.log('email: ' + req.email);
        console.log('password: ' + req.password);
        console.log('user role: ' + req.role);
        if (req.role === user_role_enum_1.UserRoles.Customer) {
            const customer = await this.findCustomerByEmailAndPassword(req.email, req.password);
            if (!customer) {
                throw new common_1.UnauthorizedException('Invalid email or password.');
            }
            payload = {
                id: customer.id,
                email: req.email,
                role: req.role,
            };
        }
        else {
            const merchant = await this.findMerchantByEmailAndPassword(req.email, req.password);
            if (!merchant) {
                throw new common_1.UnauthorizedException('Invalid email or password');
            }
            payload = {
                id: merchant.id,
                email: req.email,
                role: req.role,
            };
        }
        return this.jwtService.signAsync(payload);
    }
    async verifyAccessJWT(token) {
        try {
            return this.jwtService.verifyAsync(token);
        }
        catch {
            return null;
        }
    }
    async findCustomerByEmailAndPassword(email, password) {
        return await this.customerRepo.findOne({ where: { email, password } });
    }
    async findMerchantByEmailAndPassword(email, password) {
        return await this.merchantRepo.findOne({ where: { email, password } });
    }
};
exports.AuthService = AuthService;
exports.AuthService = AuthService = __decorate([
    (0, common_1.Injectable)(),
    __param(1, (0, typeorm_2.InjectRepository)(customer_entity_user_1.Customer)),
    __param(2, (0, typeorm_2.InjectRepository)(merchant_entity_1.Merchant)),
    __metadata("design:paramtypes", [jwt_1.JwtService,
        typeorm_1.Repository,
        typeorm_1.Repository])
], AuthService);
//# sourceMappingURL=jwt-auth-login.service.js.map