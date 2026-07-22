"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.CustomerModule = void 0;
const customer_controller_1 = require("../controllers/customer.controller");
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const customer_entity_user_1 = require("../entity/customer.entity.user");
const customer_account_service_1 = require("../services/customer-account.service");
const order_history_service_1 = require("../services/order-history.service");
const customer_entity_order_1 = require("../entity/customer.entity.order");
let CustomerModule = class CustomerModule {
};
exports.CustomerModule = CustomerModule;
exports.CustomerModule = CustomerModule = __decorate([
    (0, common_1.Module)({
        imports: [typeorm_1.TypeOrmModule.forFeature([customer_entity_user_1.Customer, customer_entity_order_1.Order])],
        controllers: [customer_controller_1.CustomerController],
        providers: [customer_account_service_1.CustomerAccountService, order_history_service_1.OrderHistoryService],
    })
], CustomerModule);
//# sourceMappingURL=customer.module.js.map