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
exports.CustomerController = void 0;
const common_1 = require("@nestjs/common");
const customer_account_service_1 = require("../services/customer-account.service");
const create_customer_request_dto_1 = require("../dto/requests/create-customer-request.dto");
const sign_in_customer_request_dto_1 = require("../dto/requests/sign-in-customer-request.dto");
const order_history_service_1 = require("../services/order-history.service");
let CustomerController = class CustomerController {
    customerAccountService;
    orderHistoryService;
    constructor(customerAccountService, orderHistoryService) {
        this.customerAccountService = customerAccountService;
        this.orderHistoryService = orderHistoryService;
    }
    async createCustomer(request) {
        return await this.customerAccountService.createCustomerAccount(request);
    }
    async loginCustomer(request) {
        return await this.customerAccountService.signIn(request);
    }
    async getPurchasedItems(customerID) {
        return await this.orderHistoryService.getCustomerOrderHistory(customerID);
    }
};
exports.CustomerController = CustomerController;
__decorate([
    (0, common_1.Post)('register'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [create_customer_request_dto_1.CreateCustomerRequest]),
    __metadata("design:returntype", Promise)
], CustomerController.prototype, "createCustomer", null);
__decorate([
    (0, common_1.Post)('login'),
    __param(0, (0, common_1.Body)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [sign_in_customer_request_dto_1.SignInCustomerRequest]),
    __metadata("design:returntype", Promise)
], CustomerController.prototype, "loginCustomer", null);
__decorate([
    (0, common_1.Get)('purchased-items/:customerID'),
    __param(0, (0, common_1.Param)('customerID')),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String]),
    __metadata("design:returntype", Promise)
], CustomerController.prototype, "getPurchasedItems", null);
exports.CustomerController = CustomerController = __decorate([
    (0, common_1.Controller)('payment-api/customers'),
    __metadata("design:paramtypes", [customer_account_service_1.CustomerAccountService,
        order_history_service_1.OrderHistoryService])
], CustomerController);
//# sourceMappingURL=customer.controller.js.map