"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.MerchantModule = void 0;
const merchant_service_1 = require("../services/merchant.service");
const merchant_controller_1 = require("../controllers/merchant.controller");
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const merchant_entity_1 = require("../entity/merchant.entity");
const merchant_product_entity_1 = require("../entity/merchant.product.entity");
let MerchantModule = class MerchantModule {
};
exports.MerchantModule = MerchantModule;
exports.MerchantModule = MerchantModule = __decorate([
    (0, common_1.Module)({
        imports: [typeorm_1.TypeOrmModule.forFeature([merchant_entity_1.Merchant, merchant_product_entity_1.Product])],
        controllers: [merchant_controller_1.MerchantController],
        providers: [merchant_service_1.MerchantService],
    })
], MerchantModule);
//# sourceMappingURL=merchant.module.js.map