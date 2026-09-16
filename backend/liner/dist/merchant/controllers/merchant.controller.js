"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
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
const jwt_auth_guard_1 = require("../../auth/jwt-auth.guard");
const jwtAuthGuardDto = __importStar(require("../../auth/jwt-auth-guard.dto"));
const user_role_enum_1 = require("../../auth/user-role.enum");
const update_product_request_dto_1 = require("../dto/requests/update-product-request.dto");
let MerchantController = class MerchantController {
    merchantService;
    constructor(merchantService) {
        this.merchantService = merchantService;
    }
    async registerMerchant(merchant) {
        console.log('MERCHANT REQUEST:', merchant);
        console.log('MERCHANT PASSWORD:', merchant?.password);
        const result = await this.merchantService.registerNewMerchant(merchant);
        return result.getID();
    }
    async addProduct(req, productReq) {
        return await this.merchantService.addProduct(productReq, req.user);
    }
    async getMerchantProducts(req, businessName) {
        if (req.user.role === user_role_enum_1.UserRoles.Merchant) {
            console.log('Getting merchant products for preview...');
            const products = await this.merchantService.getProductsForMerchant(req.user.id);
            console.log(`number of products: ${products.products.length}`);
            return products;
        }
        return await this.merchantService.getProductsForCustomer(businessName);
    }
    async updateProduct(req, updateRequest) {
        return await this.merchantService.updateProduct(updateRequest, req.user);
    }
};
exports.MerchantController = MerchantController;
__decorate([
    (0, common_1.Post)('register'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [register_merchant_request_dto_1.RegisterMerchantRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "registerMerchant", null);
__decorate([
    (0, common_1.UseGuards)(jwt_auth_guard_1.JwtAuthGuard),
    (0, common_1.Post)('add-product'),
    __param(0, (0, common_1.Req)()),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, product_request_dto_1.ProductRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "addProduct", null);
__decorate([
    (0, common_1.UseGuards)(jwt_auth_guard_1.JwtAuthGuard),
    (0, common_1.Get)('view-products'),
    __param(0, (0, common_1.Req)()),
    __param(1, (0, common_1.Query)('businessName')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, String]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "getMerchantProducts", null);
__decorate([
    (0, common_1.UseGuards)(jwt_auth_guard_1.JwtAuthGuard),
    (0, common_1.Post)('update-product'),
    __param(0, (0, common_1.Req)()),
    __param(1, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, update_product_request_dto_1.UpdateProductRequest]),
    __metadata("design:returntype", Promise)
], MerchantController.prototype, "updateProduct", null);
exports.MerchantController = MerchantController = __decorate([
    (0, common_1.Controller)('payment-api/merchants'),
    __metadata("design:paramtypes", [merchant_service_1.MerchantService])
], MerchantController);
//# sourceMappingURL=merchant.controller.js.map