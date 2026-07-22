"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.MerchantSalesModule = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const merchant_sale_entity_1 = require("../entity/merchant-sale.entity");
const rabbitmq_module_1 = require("../../rabbitmq/rabbitmq.module");
const sales_history_service_1 = require("../services/sales-history.service");
const merchant_sales_history_controller_1 = require("../controllers/merchant.sales-history.controller");
let MerchantSalesModule = class MerchantSalesModule {
};
exports.MerchantSalesModule = MerchantSalesModule;
exports.MerchantSalesModule = MerchantSalesModule = __decorate([
    (0, common_1.Module)({
        imports: [typeorm_1.TypeOrmModule.forFeature([merchant_sale_entity_1.SaleHistory]), rabbitmq_module_1.RabbitMQModule],
        controllers: [merchant_sales_history_controller_1.SalesHistoryController],
        providers: [sales_history_service_1.SalesHistoryService],
    })
], MerchantSalesModule);
//# sourceMappingURL=merchant-sales.module.js.map