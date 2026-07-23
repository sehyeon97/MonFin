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
Object.defineProperty(exports, "__esModule", { value: true });
exports.MerchantController = void 0;
const common_1 = require("@nestjs/common");
const register_merchant_request_dto_1 = require("../dto/requests/register-merchant-request.dto");
const merchant_service_1 = require("../services/merchant.service");
const product_request_dto_1 = require("../dto/requests/product.request.dto");
let MerchantController = class MerchantController {
    merchantService;
    constructor(merchantService) {
        this.merchantService = merchantService;
    }
    async registerMerchant(merchant) {
        await this.merchantService.registerNewMerchant(merchant);
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