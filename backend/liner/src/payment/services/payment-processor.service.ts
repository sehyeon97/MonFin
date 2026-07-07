/*
https://docs.nestjs.com/providers#services
*/

import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Transaction } from '../entity/payment.entity.transaction';
import { Repository } from 'typeorm';
import { CardVault } from '../entity/payment.entity.card.vault';
import { AddMerchantDebitCardRequest } from '../dto/request/merchant-card.request.dto';
import { AddDebitCardResponse } from '../dto/response/debit-card.response.dto';
import { MerchantCardResponse } from '../dto/response/merchant-card.response.dto';
import { AddPaymentMethodRequest } from '../dto/request/customer-payment-method.request.dto';
import { CustomerSavedPaymentMethodResponse } from '../dto/response/customer-payment-method.response.dto';
import { TransactionRequest } from '../dto/request/transaction.request.dto';
import { TransactionResponse } from '../dto/response/transaction.response.dto';
import { MerchantService } from '../../merchant/services/merchant.service';
import { CustomerAccountService } from '../../customer/services/customer-account.service';
import { createHmac } from 'crypto';
import { TransactionDetailsRequest } from '../dto/request/transaction.request.tobank.dto';
import { BankTransactionResponse } from '../dto/response/bank-transaction.response.dto';
import { TRANSACTION_STATUS } from '../enums/transaction-status.enum';

@Injectable()
export class PaymentProcessorService {
    private static readonly bankServerUrl = 'http://localhost:8080';
    private static readonly bankTransactionUrl =
        PaymentProcessorService.bankServerUrl +
        '/api/bank/transactions/authorize';
    private static readonly bankOTPUrl =
        PaymentProcessorService.bankServerUrl +
        '/api/bank/transactions/verify-otp';

    // i think it has to be a link to payment processor's frontend
    private static readonly callbackUrl = 'CHANGE THIS LATER';

    constructor(
        @InjectRepository(CardVault)
        private readonly cardVaultRepo: Repository<CardVault>,
        @InjectRepository(Transaction)
        private readonly transactionRepository: Repository<Transaction>,
        private readonly merchantService: MerchantService,
        private readonly customerService: CustomerAccountService,
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
        // MODIFY THIS TO REMOVE [0] LATER
        // RIGHT NOW IT HANDLES ONE TRANSACTION ONLY
        // BECAUSE MY BANK SPRING BOOT ONLY ACCEPTS ONE TRANSACTION
        const transactionData = this.combineAllTransactions(req)[0];

        // make a post request to bank/tsp backend (TransactionController.java)
        const response = await fetch(
            PaymentProcessorService.bankTransactionUrl,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    transactionDetails: transactionData,
                }),
            },
        );
        const bankResponse = (await response.json()) as BankTransactionResponse;

        const transactionResponse: TransactionResponse =
            new TransactionResponse();

        // This transaction ID will likely be an overall, unified ID, unless
        // OTP is required, then it will specifically be transaction IDs that need verification
        transactionResponse.transactionID = transactionData.transactionID;
        if (bankResponse.authorized) {
            // transaction approved
            transactionResponse.status = TRANSACTION_STATUS.APPROVED;
        } else if (bankResponse.declineReason == 'OTP Required.') {
            // otp required: use bank's callback url
            transactionResponse.status = TRANSACTION_STATUS.PENDING;
        } else {
            // fraud detected: decline transaction
            transactionResponse.status = TRANSACTION_STATUS.DECLINED;
        }

        return transactionResponse;
    }

    // This assumes that a merchant can only own one business
    private combineAllTransactions(
        data: TransactionRequest,
    ): TransactionDetailsRequest[] {
        const transactionDetails: TransactionDetailsRequest[] = [];
        const products = data.products;
        let merchantID = products[0].merchantID;
        let amountSum = 0;
        products.forEach((product) => {
            const currMerchantID = product.merchantID;
            amountSum += product.price;
            if (merchantID != currMerchantID) {
                const timestamp: string = new Date().toISOString();
                const cryptogram: string = this.generateCryptogram(
                    data.cardToken,
                    merchantID,
                    timestamp,
                    amountSum,
                );

                // what the payment processor stores
                const transaction: Transaction = new Transaction();
                transaction.cardToken = data.cardToken;
                transaction.merchantID = merchantID;
                transaction.businessName = product.businessName;
                transaction.timestamp = timestamp;
                transaction.price = amountSum;
                transaction.cryptogram = cryptogram;
                transaction.callbackUrl = PaymentProcessorService.callbackUrl;

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
                req.callbackUrl = PaymentProcessorService.callbackUrl;

                transactionDetails.push(req);
                merchantID = currMerchantID;
                amountSum = 0;
            }
        });

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
}
