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
exports.PaymentProcessorController = void 0;
const common_1 = require("@nestjs/common");
const customer_payment_method_request_dto_1 = require("../dto/request/customer-payment-method.request.dto");
const payment_processor_service_1 = require("../services/payment-processor.service");
const merchant_card_request_dto_1 = require("../dto/request/merchant-card.request.dto");
const transaction_request_dto_1 = require("../dto/request/transaction.request.dto");
let PaymentProcessorController = class PaymentProcessorController {
    paymentProcessorService;
    constructor(paymentProcessorService) {
        this.paymentProcessorService = paymentProcessorService;
    }
    async addDebitForMerchant(req) {
        return this.paymentProcessorService.addDebitCardForMerchant(req);
    }
    async viewPaymentMethods(customerID) {
        return this.paymentProcessorService.getSavedPaymentMethods(customerID);
    }
    async addPaymentMethod(paymentMethod) {
        return this.paymentProcessorService.savePaymentMethod(paymentMethod);
    }
    async createTransaction(req) {
        return await this.paymentProcessorService.compileTransaction(req);
    }
    async saveTransactionResults(transactionResults) {
        await this.paymentProcessorService.finalizeTransactions(transactionResults);
    }
};
exports.PaymentProcessorController = PaymentProcessorController;
__decorate([
    (0, common_1.Post)('merchant/debit-card'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [merchant_card_request_dto_1.AddMerchantDebitCardRequest]),
    __metadata("design:returntype", Promise)
], PaymentProcessorController.prototype, "addDebitForMerchant", null);
__decorate([
    (0, common_1.Get)('customer/payment-methods/:id'),
    __param(0, (0, common_1.Param)('id')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], PaymentProcessorController.prototype, "viewPaymentMethods", null);
__decorate([
    (0, common_1.Post)('customer/save/payment-method'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [customer_payment_method_request_dto_1.AddPaymentMethodRequest]),
    __metadata("design:returntype", Promise)
], PaymentProcessorController.prototype, "addPaymentMethod", null);
__decorate([
    (0, common_1.Post)('checkout'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [transaction_request_dto_1.TransactionRequest]),
    __metadata("design:returntype", Promise)
], PaymentProcessorController.prototype, "createTransaction", null);
__decorate([
    (0, common_1.Post)('bank-otp'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Array]),
    __metadata("design:returntype", Promise)
], PaymentProcessorController.prototype, "saveTransactionResults", null);
exports.PaymentProcessorController = PaymentProcessorController = __decorate([
    (0, common_1.Controller)('payment-api/payment-processor'),
    __metadata("design:paramtypes", [payment_processor_service_1.PaymentProcessorService])
], PaymentProcessorController);
//# sourceMappingURL=payment-processor.controller.js.map