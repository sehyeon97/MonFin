/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { CustomerAccountService } from '../services/customer-account.service';
import { Customer } from '../entity/customer.entity.user';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';
import { OrderHistoryService } from '../services/order-history.service';
import { PurchasedItem } from '../dto/responses/purchased-item.response.dto';

// NestJS automatically attaches '/' routing
@Controller('payment-api/customers')
export class CustomerController {
    constructor(
        private readonly customerAccountService: CustomerAccountService,
        private readonly orderHistoryService: OrderHistoryService,
    ) {}

    @Post('register')
    public async createCustomer(
        @Body() request: CreateCustomerRequest,
    ): Promise<Customer> {
        return await this.customerAccountService.createCustomerAccount(request);
    }

    // *** REFACTORED TO JWT AUTH *** Testing in progress. . . then delete after success
    // @Post('login')
    // public async loginCustomer(
    //     @Body() request: SignInCustomerRequest,
    // ): Promise<string> {
    //     return await this.customerAccountService.signIn(request);
    // }

    @Get('purchased-items/:customerID')
    public async getPurchasedItems(
        @Param('customerID') customerID: string,
    ): Promise<PurchasedItem[]> {
        return await this.orderHistoryService.getCustomerOrderHistory(
            customerID,
        );
    }
}
