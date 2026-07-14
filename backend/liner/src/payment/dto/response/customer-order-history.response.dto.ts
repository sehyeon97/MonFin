import { CustomerCompletedTransactions } from './customer-completed-transactions.response.dto';

export class CustomerOrderHistory {
    customerID!: string;
    transactions!: CustomerCompletedTransactions[];
}
