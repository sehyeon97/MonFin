import { CustomerAccountService } from '../services/customer-account.service';
import { Customer } from '../entity/customer.entity.user';
import { CreateCustomerRequest } from '../dto/requests/create-customer-request.dto';
import { OrderHistoryService } from '../services/order-history.service';
import { PurchasedItem } from '../dto/responses/purchased-item.response.dto';
export declare class CustomerController {
    private readonly customerAccountService;
    private readonly orderHistoryService;
    constructor(customerAccountService: CustomerAccountService, orderHistoryService: OrderHistoryService);
    createCustomer(request: CreateCustomerRequest): Promise<Customer>;
    getPurchasedItems(customerID: string): Promise<PurchasedItem[]>;
}
