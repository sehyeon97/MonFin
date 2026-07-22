"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaymentProcessorModule = void 0;
const typeorm_1 = require("@nestjs/typeorm");
const payment_entity_card_vault_1 = require("../entity/payment.entity.card.vault");
const payment_entity_transaction_1 = require("../entity/payment.entity.transaction");
const payment_processor_controller_1 = require("./../controllers/payment-processor.controller");
const payment_processor_service_1 = require("./../services/payment-processor.service");
const common_1 = require("@nestjs/common");
const rabbitmq_module_1 = require("../../rabbitmq/rabbitmq.module");
let PaymentProcessorModule = class PaymentProcessorModule {
};
exports.PaymentProcessorModule = PaymentProcessorModule;
exports.PaymentProcessorModule = PaymentProcessorModule = __decorate([
    (0, common_1.Module)({
        imports: [
            typeorm_1.TypeOrmModule.forFeature([payment_entity_card_vault_1.CardVault, payment_entity_transaction_1.Transaction]),
            rabbitmq_module_1.RabbitMQModule,
        ],
        controllers: [payment_processor_controller_1.PaymentProcessorController],
        providers: [payment_processor_service_1.PaymentProcessorService],
    })
], PaymentProcessorModule);
//# sourceMappingURL=payment-processor.module.js.map