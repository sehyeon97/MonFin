/*
https://docs.nestjs.com/providers#services
*/

import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Customer } from '../entity/customer.entity.user';
import { Repository } from 'typeorm';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';

@Injectable()
export class CustomerAccountService {
    constructor(
        @InjectRepository(Customer)
        private readonly customerRepository: Repository<Customer>,
    ) {}

    public async createCustomerAccount(
        req: CreateCustomerRequest,
    ): Promise<Customer> {
        const customer = this.customerRepository.create({
            email: req.email,
            password: req.password,
            billingAddress: req.billingAddress,
            billingCity: req.billingCity,
            billingState: req.billingState,
            billingZip: req.billingZip,
        });

        return this.customerRepository.save(customer);
    }
}
