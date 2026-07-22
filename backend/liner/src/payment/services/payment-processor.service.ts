/*
https://docs.nestjs.com/providers#services
*/

import { Inject, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Transaction } from '../entity/payment.entity.transaction';
import { In, Repository } from 'typeorm';
import { CardVault } from '../entity/payment.entity.card.vault';
import { AddMerchantDebitCardRequest } from '../dto/request/merchant-card.request.dto';
import { AddDebitCardResponse } from '../dto/response/debit-card.response.dto';
import { MerchantCardResponse } from '../dto/response/merchant-card.response.dto';
import { AddPaymentMethodRequest } from '../dto/request/customer-payment-method.request.dto';
import { CustomerSavedPaymentMethodResponse } from '../dto/response/customer-payment-method.response.dto';
import { TransactionRequest } from '../dto/request/transaction.request.dto';
import { TransactionResponse } from '../dto/response/transaction.response.dto';
import { createHmac } from 'crypto';
import { TransactionDetailsRequest } from '../dto/request/transaction.request.tobank.dto';
import { BankTransactionResponse } from '../dto/response/transaction/bank-transaction.response.dto';
import { TRANSACTION_STATUS } from '../enums/transaction-status.enum';
import { BankOTPTransactionResult } from '../dto/request/bank-otp-result.request.dto';
import { ClientProxy } from '@nestjs/microservices';
import { BankResponseData } from '../dto/bank-response-data.dto';
import { TransactionData } from '../dto/transaction-data.dto';
import { CardAuthorizationResponse } from '../dto/response/transaction/bank-card-authorization.response.dto';

@Injectable()
export class PaymentProcessorService {
    private static readonly bankServerUrl = 'http://localhost:8080';
    private static readonly bankTransactionUrl =
        PaymentProcessorService.bankServerUrl +
        '/api/bank/transactions/authorize';
    private static readonly bankOTPUrl =
        PaymentProcessorService.bankServerUrl +
        '/api/bank/transactions/verify-otp';

    private static readonly clientUrl = 'http://localhost:5173';
    private static readonly serverUrl = 'http://localhost:3000';
    private static readonly redirectUrl = '/purchased-items';
    private static readonly callbackUrl = '/payment-processor/bank-otp';

    constructor(
        @InjectRepository(CardVault)
        private readonly cardVaultRepo: Repository<CardVault>,
        @InjectRepository(Transaction)
        private readonly transactionRepository: Repository<Transaction>,
        @Inject('PAYMENT_EVENTS')
        private readonly client: ClientProxy,
    ) {}

    ///////////////////////// ### *** MERCHANT *** ### /////////////////////////
    // which card the merchant receives payment; can only be one
    public async addDebitCardForMerchant(
        debitCard: AddMerchantDebitCardRequest,
    ): Promise<MerchantCardResponse> {
        if (await this.hasDebitCard(debitCard.merchantID)) {
            return {
                status: 'Rejected.',
                card: new AddDebitCardResponse(), // will serialize to {}
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
        const result: AddDebitCardResponse = new AddDebitCardResponse();
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

    private async hasDebitCard(merchantID: string): Promise<boolean> {
        const debitCard: CardVault | null = await this.cardVaultRepo.findOne({
            where: { userID: merchantID },
        });

        return debitCard != null;
    }
    ///////////////////////// ### *** MERCHANT *** ### /////////////////////////

    ///////////////////////// ### *** CUSTOMER *** ### /////////////////////////
    public async getSavedPaymentMethods(
        customerID: string,
    ): Promise<CustomerSavedPaymentMethodResponse[]> {
        const paymentMethods = await this.cardVaultRepo.find({
            where: { id: customerID },
        });

        return paymentMethods.map((paymentMethod) => ({
            id: paymentMethod.id,
            network: paymentMethod.network,
            lastFour: paymentMethod.lastFour,
            expMonth: paymentMethod.expMonth,
            expYear: paymentMethod.expYear,
        }));
    }

    public async savePaymentMethod(
        paymentMethod: AddPaymentMethodRequest,
    ): Promise<void> {
        const pm = this.cardVaultRepo.create({
            userID: paymentMethod.customerID,
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
    ///////////////////////// ### *** CUSTOMER *** ### /////////////////////////

    ///////////////////////// ### *** PROCESSOR *** ### /////////////////////////
    public async compileTransaction(
        req: TransactionRequest,
    ): Promise<TransactionResponse> {
        const combinedTransactions = await this.combineAllTransactions(req);

        // make a post request to bank/tsp backend (TransactionController.java)
        const response = await fetch(
            PaymentProcessorService.bankTransactionUrl,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    transactionDetails: combinedTransactions,
                }),
            },
        );
        const bankResponse =
            (await response.json()) as BankTransactionResponse[];

        const approvedTransactions: TransactionData[] = [];
        const otpRequiredTransactions: TransactionData[] = [];
        const declinedTransactions: TransactionData[] = [];
        bankResponse.forEach((res) => {
            // const transactionData: TransactionData = res.transactionData;
            const cardAuthorizationData: CardAuthorizationResponse =
                res.authorizationData;
            const transactionData: TransactionData = res.transactionData;

            if (cardAuthorizationData.authorized) {
                // transaction approved
                approvedTransactions.push(transactionData);
            } else if (cardAuthorizationData.declineReason == 'OTP Required.') {
                otpRequiredTransactions.push(transactionData);
            } else {
                declinedTransactions.push(transactionData);
            }
        });

        await this.emitEvent(
            approvedTransactions,
            'payment.approved',
            TRANSACTION_STATUS.APPROVED,
        );

        await this.emitEvent(
            declinedTransactions,
            'payment.declined',
            TRANSACTION_STATUS.DECLINED,
        );

        const transactionResponse: TransactionResponse =
            new TransactionResponse();
        transactionResponse.bankCallbackUrl =
            otpRequiredTransactions.length > 0 ? 'bank-frontend-url' : '';
        return transactionResponse;
    }

    // This assumes that a merchant can only own one business
    private async combineAllTransactions(
        data: TransactionRequest,
    ): Promise<TransactionDetailsRequest[]> {
        const transactionsToStore: Transaction[] = [];

        const transactionDetails: TransactionDetailsRequest[] = [];
        const products = data.products;
        let merchantID = products[0].merchantID;
        let amountSum = 0;
        products.forEach((product) => {
            const currMerchantID = product.merchantID;
            amountSum += product.price * product.count;
            if (merchantID != currMerchantID) {
                const timestamp: string = new Date().toISOString();
                const cryptogram: string = this.generateCryptogram(
                    data.cardToken,
                    merchantID,
                    timestamp,
                    amountSum,
                );

                // what the payment processor stores
                // not getting stored yet
                const transaction: Transaction = new Transaction();
                transaction.cardToken = data.cardToken;
                transaction.customerID = data.customerID;
                transaction.merchantID = merchantID;
                transaction.businessName = product.businessName;
                transaction.timestamp = timestamp;
                transaction.price = amountSum;
                transaction.itemName = product.productName;
                transaction.quantity = product.count;
                transactionsToStore.push(transaction);

                // what the bank is given
                const req: TransactionDetailsRequest =
                    new TransactionDetailsRequest();
                req.transactionID = transaction.id;
                req.cardToken = data.cardToken;
                req.merchantID = merchantID;
                req.merchantName = product.businessName;
                req.timestamp = timestamp;
                req.amount = amountSum;
                req.cryptogram = cryptogram;
                req.redirectUrl =
                    PaymentProcessorService.clientUrl +
                    PaymentProcessorService.redirectUrl;
                req.serverUrl =
                    PaymentProcessorService.serverUrl +
                    PaymentProcessorService.callbackUrl;

                transactionDetails.push(req);
                merchantID = currMerchantID;
                amountSum = 0;
            }
        });

        await this.transactionRepository.insert(transactionsToStore);
        return transactionDetails;
    }

    private generateCryptogram(
        cardToken: string,
        merchantID: string,
        timestamp: string,
        totalAmount: number,
    ): string {
        const param: string =
            merchantID + '|' + timestamp + '|' + totalAmount.toString();
        const hmac: string = createHmac('sha512', cardToken)
            .update(param, 'utf-8')
            .digest('hex'); // automatically converts to hex string
        return hmac;
    }

    // This would never be called if OTP id was invalid (not otp string)
    public async finalizeTransactions(
        req: BankOTPTransactionResult[],
    ): Promise<void> {
        // key: transaction id | value: transaction details
        const approvedTransactions: TransactionData[] = [];
        const declinedTransactions: TransactionData[] = [];
        for (const request of req) {
            const transactionData: TransactionData = request.transactionData;
            const bankResponse: BankResponseData = request.bankResData;
            const oTPApproved: boolean = bankResponse.authorized;

            if (oTPApproved) {
                approvedTransactions.push(transactionData);
            } else {
                declinedTransactions.push(transactionData);
            }
        }

        // change approved transactions' statuses to processing
        // emit payment approved event
        await this.emitEvent(
            approvedTransactions,
            'payment.approved',
            TRANSACTION_STATUS.APPROVED,
        );

        // change declined transactions' statuses to declined
        // emit payment declined event
        await this.emitEvent(
            declinedTransactions,
            'payment.declined',
            TRANSACTION_STATUS.DECLINED,
        );
    }

    private async emitEvent(
        transactions: TransactionData[],
        eventType: string,
        transactionStatus: TRANSACTION_STATUS,
    ): Promise<void> {
        if (transactions.length > 0) {
            await this.transactionRepository.update(
                {
                    id: In(
                        transactions.map(
                            (transaction) => transaction.transactionID,
                        ),
                    ),
                },
                {
                    status: transactionStatus,
                },
            );
            this.client.emit(eventType, transactions);
        }
    }
}
