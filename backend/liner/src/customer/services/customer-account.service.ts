/*
https://docs.nestjs.com/providers#services
*/

import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Customer } from '../entity/customer.entity.user';
import { Repository } from 'typeorm';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';
import { SignInCustomerRequest } from '../dto/requests/sign-in-customer-request.dto';

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
            verified: req.verified,
            billingAddress: req.billingAddress,
            billingCity: req.billingCity,
            billingState: req.billingState,
            billingZip: req.billingZip,
        });

        return await this.customerRepository.save(customer);
    }

    public async signIn(req: SignInCustomerRequest): Promise<string> {
        const customer: Customer | null = await this.customerRepository.findOne(
            {
                where: { email: req.email, password: req.password },
            },
        );

        return customer ? customer.id : '';
    }
}
