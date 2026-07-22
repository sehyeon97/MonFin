import { TransactionData } from '../transaction-data.dto';

export class TransactionResponse {
    bankCallbackUrl!: string; // when otp is required. must be the bank frontend url
    approvedTransactionClientData!: TransactionData[];
    declinedTransactionClientData!: TransactionData[];
}
