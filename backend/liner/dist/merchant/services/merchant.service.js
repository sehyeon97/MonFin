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
exports.MerchantService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const merchant_entity_1 = require("../entity/merchant.entity");
const typeorm_2 = require("typeorm");
const merchant_product_entity_1 = require("../entity/merchant.product.entity");
const product_response_dto_1 = require("../dto/responses/product.response.dto");
let MerchantService = class MerchantService {
    merchantRepository;
    productRepository;
    constructor(merchantRepository, productRepository) {
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
    }
    async registerNewMerchant(newMerchant) {
        const merchant = this.merchantRepository.create(newMerchant);
        merchant.verified = false;
        await this.merchantRepository.save(merchant);
    }
    async signIn(req) {
        const merchant = await this.merchantRepository.findOne({
            where: { email: req.email, password: req.password },
        });
        return merchant ? merchant.id : '';
    }
    async addProduct(req) {
        const product = this.productRepository.create(req);
        await this.productRepository.save(product);
        return {
            businessName: product.businessName,
            brand: product.brand,
            price: product.price,
            desc: product.desc,
            count: product.count,
        };
    }
    async getProducts(merchantID) {
        const products = await this.productRepository.find({
            where: { merchantID: merchantID },
        });
        const merchantProducts = products.map((product) => {
            const res = new product_response_dto_1.ProductResponse();
            res.businessName = product.businessName;
            res.brand = product.brand;
            res.price = product.price;
            res.desc = product.desc;
            res.count = product.count;
            return res;
        });
        return {
            merchantID: merchantID,
            products: merchantProducts,
        };
    }
    async reduceProductCount(merchantID, productID, num) {
        const product = await this.productRepository.findOne({
            where: { id: productID, merchantID: merchantID },
        });
        if (product) {
            product.count -= num;
            await this.productRepository.save(product);
        }
    }
};
exports.MerchantService = MerchantService;
exports.MerchantService = MerchantService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(merchant_entity_1.Merchant)),
    __param(1, (0, typeorm_1.InjectRepository)(merchant_product_entity_1.Product)),
    __metadata("design:paramtypes", [typeorm_2.Repository,
        typeorm_2.Repository])
], MerchantService);
//# sourceMappingURL=merchant.service.js.map