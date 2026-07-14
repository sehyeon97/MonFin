/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { CustomerSavedPaymentMethodResponse } from '../dto/response/customer-payment-method.response.dto';
import { AddPaymentMethodRequest } from '../dto/request/customer-payment-method.request.dto';
import { PaymentProcessorService } from '../services/payment-processor.service';
import { AddMerchantDebitCardRequest } from '../dto/request/merchant-card.request.dto';
import { MerchantCardResponse } from '../dto/response/merchant-card.response.dto';
import { BankOTPTransactionResult } from '../dto/request/bank-otp-result.request.dto';
import { TransactionRequest } from '../dto/request/transaction.request.dto';
import { TransactionResponse } from '../dto/response/transaction.response.dto';

@Controller('payment-processor')
export class PaymentProcessorController {
    constructor(
        private readonly paymentProcessorService: PaymentProcessorService,
    ) {}

    ///////////////////////// ### *** MERCHANT *** ### /////////////////////////
    @Post('merchant/debit-card')
    public async addDebitForMerchant(
        @Body() req: AddMerchantDebitCardRequest,
    ): Promise<MerchantCardResponse> {
        return this.paymentProcessorService.addDebitCardForMerchant(req);
    }
    ///////////////////////// ### *** MERCHANT *** ### /////////////////////////

    ///////////////////////// ### *** CUSTOMER *** ### /////////////////////////
    @Get('customer/payment-methods')
    public async viewPaymentMethods(
        @Param('id') customerID: string,
    ): Promise<CustomerSavedPaymentMethodResponse[]> {
        return this.paymentProcessorService.getSavedPaymentMethods(customerID);
    }

    @Post('customer/save/payment-method')
    public async addPaymentMethod(
        @Body() paymentMethod: AddPaymentMethodRequest,
    ): Promise<void> {
        return this.paymentProcessorService.savePaymentMethod(paymentMethod);
    }
    ///////////////////////// ### *** CUSTOMER *** ### /////////////////////////

    ///////////////////////// ### *** PROCESSOR *** ### /////////////////////////
    @Post('checkout')
    public async createTransaction(
        @Body() req: TransactionRequest,
    ): Promise<TransactionResponse[]> {
        return await this.paymentProcessorService.compileTransaction(req);
    }

    @Post('bank-otp')
    public async saveTransactionResults(
        @Body() transactionResults: BankOTPTransactionResult[],
    ): Promise<void> {
        await this.paymentProcessorService.finalizeTransactions(
            transactionResults,
        );
    }
}
