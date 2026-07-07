/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, Post } from '@nestjs/common';
import { CustomerAccountService } from '../services/customer-account.service';
import { Customer } from '../entity/customer.entity.user';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';

// NestJS automatically attaches '/' routing
@Controller('customers')
export class CustomerController {
    constructor(
        private readonly customerAccountService: CustomerAccountService,
    ) {}

    @Post('new')
    public async createCustomer(
        @Body() request: CreateCustomerRequest,
    ): Promise<Customer> {
        return this.customerAccountService.createCustomerAccount(request);
    }
}
