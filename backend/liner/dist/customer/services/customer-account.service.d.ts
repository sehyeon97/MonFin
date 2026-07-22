import { Customer } from '../entity/customer.entity.user';
import { Repository } from 'typeorm';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';
import { SignInCustomerRequest } from '../dto/requests/sign-in-customer-request.dto';
export declare class CustomerAccountService {
    private readonly customerRepository;
    constructor(customerRepository: Repository<Customer>);
    createCustomerAccount(req: CreateCustomerRequest): Promise<Customer>;
    signIn(req: SignInCustomerRequest): Promise<string>;
}
