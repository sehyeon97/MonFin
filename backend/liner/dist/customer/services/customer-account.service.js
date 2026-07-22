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
exports.CustomerAccountService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const customer_entity_user_1 = require("../entity/customer.entity.user");
const typeorm_2 = require("typeorm");
let CustomerAccountService = class CustomerAccountService {
    customerRepository;
    constructor(customerRepository) {
        this.customerRepository = customerRepository;
    }
    async createCustomerAccount(req) {
        const customer = this.customerRepository.create({
            email: req.email,
            password: req.password,
            verified: req.verified,
            billingAddress: req.billingAddress,
            billingCity: req.billingCity,
            billingState: req.billingState,
            billingZip: req.billingZip,
        });
        return await this.customerRepository.save(customer);
    }
    async signIn(req) {
        const customer = await this.customerRepository.findOne({
            where: { email: req.email, password: req.password },
        });
        return customer ? customer.id : '';
    }
};
exports.CustomerAccountService = CustomerAccountService;
exports.CustomerAccountService = CustomerAccountService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(customer_entity_user_1.Customer)),
    __metadata("design:paramtypes", [typeorm_2.Repository])
], CustomerAccountService);
//# sourceMappingURL=customer-account.service.js.map