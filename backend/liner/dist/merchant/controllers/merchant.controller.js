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
exports.MerchantController = void 0;
const common_1 = require("@nestjs/common");
const register_merchant_request_dto_1 = require("../dto/requests/register-merchant-request.dto");
const merchant_service_1 = require("../services/merchant.service");
const product_request_dto_1 = require("../dto/requests/product.request.dto");
const sign_in_merchant_request_dto_1 = require("../dto/requests/sign-in-merchant.request.dto");
let MerchantController = class MerchantController {
    merchantService;
    constructor(merchantService) {
        this.merchantService = merchantService;
    }
    async registerMerchant(merchant) {
        await this.merchantService.registerNewMerchant(merchant);
    }
    async loginMerchant(request) {
        return await this.merchantService.signIn(request);
    }
    async addProduct(req) {
        return await this.merchantService.addProduct(req);
    }
    async getMerchantProducts(merchantID) {
        return await this.merchantService.getProducts(merchantID);
    }
};
exports.MerchantController = MerchantController;
__decorate([
    (0, common_1.Post)('register'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [register_merchant_request_dto_1.RegisterMerchantRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "registerMerchant", null);
__decorate([
    (0, common_1.Post)('login'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [sign_in_merchant_request_dto_1.SignInMerchantRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "loginMerchant", null);
__decorate([
    (0, common_1.Post)('add-product'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [product_request_dto_1.ProductRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "addProduct", null);
__decorate([
    (0, common_1.Get)('view-products'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "getMerchantProducts", null);
exports.MerchantController = MerchantController = __decorate([
    (0, common_1.Controller)('payment-api/merchants'),
    __metadata("design:paramtypes", [merchant_service_1.MerchantService])
], MerchantController);
//# sourceMappingURL=merchant.controller.js.map