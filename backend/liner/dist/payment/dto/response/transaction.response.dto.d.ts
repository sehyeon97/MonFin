import { TransactionData } from '../transaction-data.dto';
export declare class TransactionResponse {
    bankCallbackUrl: string;
    approvedTransactionClientData: TransactionData[];
    declinedTransactionClientData: TransactionData[];
}
