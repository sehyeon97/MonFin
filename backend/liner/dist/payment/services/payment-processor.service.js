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
var PaymentProcessorService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaymentProcessorService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const payment_entity_transaction_1 = require("../entity/payment.entity.transaction");
const typeorm_2 = require("typeorm");
const payment_entity_card_vault_1 = require("../entity/payment.entity.card.vault");
const debit_card_response_dto_1 = require("../dto/response/debit-card.response.dto");
const transaction_response_dto_1 = require("../dto/response/transaction.response.dto");
const crypto_1 = require("crypto");
const transaction_request_tobank_dto_1 = require("../dto/request/transaction.request.tobank.dto");
const transaction_status_enum_1 = require("../enums/transaction-status.enum");
const microservices_1 = require("@nestjs/microservices");
let PaymentProcessorService = class PaymentProcessorService {
    static { PaymentProcessorService_1 = this; }
    cardVaultRepo;
    transactionRepository;
    client;
    static bankServerUrl = 'http://localhost:8080';
    static bankTransactionUrl = PaymentProcessorService_1.bankServerUrl +
        '/api/bank/transactions/authorize';
    static bankOTPUrl = PaymentProcessorService_1.bankServerUrl +
        '/api/bank/transactions/verify-otp';
    static clientUrl = 'http://localhost:5173';
    static serverUrl = 'http://localhost:3000';
    static redirectUrl = '/purchased-items';
    static callbackUrl = '/payment-processor/bank-otp';
    constructor(cardVaultRepo, transactionRepository, client) {
        this.cardVaultRepo = cardVaultRepo;
        this.transactionRepository = transactionRepository;
        this.client = client;
    }
    async addDebitCardForMerchant(debitCard) {
        if (await this.hasDebitCard(debitCard.merchantID)) {
            return {
                status: 'Rejected.',
                card: new debit_card_response_dto_1.AddDebitCardResponse(),
            };
        }
        const network = debitCard.network;
        const fullName = debitCard.fullName;
        const expMonth = debitCard.expMonth;
        const expYear = debitCard.expYear;
        const dc = this.cardVaultRepo.create({
            userID: debitCard.merchantID,
            cardToken: debitCard.cardToken,
            lastFour: debitCard.lastFour,
            fullName: fullName,
            network: network,
            expMonth: expMonth,
            expYear: expYear,
        });
        dc.lastUsedAt = new Date().toLocaleDateString();
        await this.cardVaultRepo.save(dc);
        const result = new debit_card_response_dto_1.AddDebitCardResponse();
        result.network = network;
        result.fullName = fullName;
        result.expMonth = expMonth;
        result.expYear = expYear;
        result.lastUsedAt = new Date().toLocaleDateString();
        return {
            status: 'Success.',
            card: result,
        };
    }
    async hasDebitCard(merchantID) {
        const debitCard = await this.cardVaultRepo.findOne({
            where: { userID: merchantID },
        });
        return debitCard != null;
    }
    async getSavedPaymentMethods(customerID) {
        const paymentMethods = await this.cardVaultRepo.find({
            where: { userID: customerID },
        });
        console.log('paymentMethods:', paymentMethods);
        console.log('is array:', Array.isArray(paymentMethods));
        const arr = paymentMethods.map((paymentMethod) => ({
            id: paymentMethod.id,
            network: paymentMethod.network,
            lastFour: paymentMethod.lastFour,
            expMonth: paymentMethod.expMonth,
            expYear: paymentMethod.expYear,
        }));
        console.log(arr);
        return arr;
    }
    async savePaymentMethod(paymentMethod, customerID) {
        const pm = this.cardVaultRepo.create({
            userID: customerID,
            cardToken: paymentMethod.cardToken,
            lastFour: paymentMethod.lastFour,
            fullName: paymentMethod.fullName,
            network: paymentMethod.network,
            expMonth: paymentMethod.expMonth,
            expYear: paymentMethod.expYear,
        });
        pm.lastUsedAt = new Date().toLocaleDateString();
        await this.cardVaultRepo.save(paymentMethod);
    }
    async compileTransaction(req) {
        const combinedTransactions = await this.combineAllTransactions(req);
        const response = await fetch(PaymentProcessorService_1.bankTransactionUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                transactionDetails: combinedTransactions,
            }),
        });
        const bankResponse = (await response.json());
        const approvedTransactions = [];
        const otpRequiredTransactions = [];
        const declinedTransactions = [];
        bankResponse.forEach((res) => {
            const cardAuthorizationData = res.authorizationData;
            const transactionData = res.transactionData;
            if (cardAuthorizationData.authorized) {
                approvedTransactions.push(transactionData);
            }
            else if (cardAuthorizationData.declineReason == 'OTP Required.') {
                otpRequiredTransactions.push(transactionData);
            }
            else {
                declinedTransactions.push(transactionData);
            }
        });
        await this.emitEvent(approvedTransactions, 'payment.approved', transaction_status_enum_1.TRANSACTION_STATUS.APPROVED);
        await this.emitEvent(declinedTransactions, 'payment.declined', transaction_status_enum_1.TRANSACTION_STATUS.DECLINED);
        const transactionResponse = new transaction_response_dto_1.TransactionResponse();
        transactionResponse.bankCallbackUrl =
            otpRequiredTransactions.length > 0 ? 'bank-frontend-url' : '';
        return transactionResponse;
    }
    async combineAllTransactions(data) {
        const transactionsToStore = [];
        const transactionDetails = [];
        const products = data.products;
        let merchantID = products[0].merchantID;
        let amountSum = 0;
        products.forEach((product) => {
            const currMerchantID = product.merchantID;
            amountSum += product.price * product.count;
            if (merchantID != currMerchantID) {
                const timestamp = new Date().toISOString();
                const cryptogram = this.generateCryptogram(data.cardToken, merchantID, timestamp, amountSum);
                const transaction = new payment_entity_transaction_1.Transaction();
                transaction.cardToken = data.cardToken;
                transaction.customerID = data.customerID;
                transaction.merchantID = merchantID;
                transaction.businessName = product.businessName;
                transaction.timestamp = timestamp;
                transaction.price = amountSum;
                transaction.itemName = product.productName;
                transaction.quantity = product.count;
                transactionsToStore.push(transaction);
                const req = new transaction_request_tobank_dto_1.TransactionDetailsRequest();
                req.transactionID = transaction.id;
                req.cardToken = data.cardToken;
                req.merchantID = merchantID;
                req.merchantName = product.businessName;
                req.timestamp = timestamp;
                req.amount = amountSum;
                req.cryptogram = cryptogram;
                req.redirectUrl =
                    PaymentProcessorService_1.clientUrl +
                        PaymentProcessorService_1.redirectUrl;
                req.serverUrl =
                    PaymentProcessorService_1.serverUrl +
                        PaymentProcessorService_1.callbackUrl;
                transactionDetails.push(req);
                merchantID = currMerchantID;
                amountSum = 0;
            }
        });
        await this.transactionRepository.insert(transactionsToStore);
        return transactionDetails;
    }
    generateCryptogram(cardToken, merchantID, timestamp, totalAmount) {
        const param = merchantID + '|' + timestamp + '|' + totalAmount.toString();
        const hmac = (0, crypto_1.createHmac)('sha512', cardToken)
            .update(param, 'utf-8')
            .digest('hex');
        return hmac;
    }
    async finalizeTransactions(req) {
        const approvedTransactions = [];
        const declinedTransactions = [];
        for (const request of req) {
            const transactionData = request.transactionData;
            const bankResponse = request.bankResData;
            const oTPApproved = bankResponse.authorized;
            if (oTPApproved) {
                approvedTransactions.push(transactionData);
            }
            else {
                declinedTransactions.push(transactionData);
            }
        }
        await this.emitEvent(approvedTransactions, 'payment.approved', transaction_status_enum_1.TRANSACTION_STATUS.APPROVED);
        await this.emitEvent(declinedTransactions, 'payment.declined', transaction_status_enum_1.TRANSACTION_STATUS.DECLINED);
    }
    async emitEvent(transactions, eventType, transactionStatus) {
        if (transactions.length > 0) {
            await this.transactionRepository.update({
                id: (0, typeorm_2.In)(transactions.map((transaction) => transaction.transactionID)),
            }, {
                status: transactionStatus,
            });
            this.client.emit(eventType, transactions);
        }
    }
};
exports.PaymentProcessorService = PaymentProcessorService;
exports.PaymentProcessorService = PaymentProcessorService = PaymentProcessorService_1 = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(payment_entity_card_vault_1.CardVault)),
    __param(1, (0, typeorm_1.InjectRepository)(payment_entity_transaction_1.Transaction)),
    __param(2, (0, common_1.Inject)('PAYMENT_EVENTS')),
    __metadata("design:paramtypes", [typeorm_2.Repository,
        typeorm_2.Repository,
        microservices_1.ClientProxy])
], PaymentProcessorService);
//# sourceMappingURL=payment-processor.service.js.map