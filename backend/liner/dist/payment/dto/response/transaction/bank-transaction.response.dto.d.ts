import { CardAuthorizationResponse } from './bank-card-authorization.response.dto';
import { TransactionData } from './transaction-data.response.dto';
export declare class BankTransactionResponse {
    transactionData: TransactionData;
    authorizationData: CardAuthorizationResponse;
}
