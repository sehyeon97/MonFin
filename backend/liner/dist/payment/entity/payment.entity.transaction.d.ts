import { TRANSACTION_STATUS } from '../enums/transaction-status.enum';
export declare class Transaction {
    id: string;
    cardToken: string;
    customerID: string;
    merchantID: string;
    businessName: string;
    timestamp: string;
    price: number;
    itemName: string;
    brand: string;
    quantity: number;
    status: TRANSACTION_STATUS;
}
