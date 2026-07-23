/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, Get, Post, Req, UseGuards } from '@nestjs/common';
import { CustomerSavedPaymentMethodResponse } from '../dto/response/customer-payment-method.response.dto';
import { AddPaymentMethodRequest } from '../dto/request/customer-payment-method.request.dto';
import { PaymentProcessorService } from '../services/payment-processor.service';
import { AddMerchantDebitCardRequest } from '../dto/request/merchant-card.request.dto';
import { MerchantCardResponse } from '../dto/response/merchant-card.response.dto';
import { BankOTPTransactionResult } from '../dto/request/bank-otp-result.request.dto';
import { TransactionRequest } from '../dto/request/transaction.request.dto';
import { TransactionResponse } from '../dto/response/transaction.response.dto';
import { JwtAuthGuard } from '../../auth/jwt-auth.guard';
import type { AuthenticatedRequest } from '../../auth/jwt-auth-guard.dto';

@Controller('payment-api/payment-processor')
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
    @UseGuards(JwtAuthGuard)
    public async viewPaymentMethods(
        @Req() request: AuthenticatedRequest,
    ): Promise<CustomerSavedPaymentMethodResponse[]> {
        return this.paymentProcessorService.getSavedPaymentMethods(
            request.user.id,
        );
    }

    @Post('customer/save/payment-method')
    @UseGuards(JwtAuthGuard)
    public async addPaymentMethod(
        @Req() request: AuthenticatedRequest,
        @Body() paymentMethod: AddPaymentMethodRequest,
    ): Promise<void> {
        return this.paymentProcessorService.savePaymentMethod(
            paymentMethod,
            request.user.id,
        );
    }
    ///////////////////////// ### *** CUSTOMER *** ### /////////////////////////

    ///////////////////////// ### *** PROCESSOR *** ### /////////////////////////
    @Post('checkout')
    public async createTransaction(
        @Body() req: TransactionRequest,
    ): Promise<TransactionResponse> {
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
