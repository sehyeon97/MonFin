import { CardAuthorizationResponse } from "./transaction-response/card-authorization.response";
import { TransactionData } from "./transaction-response/transaction-data.response";

// Can come from authorizing a transaction or verifying otp then authorizing a transaction
export class TransactionResponse {
  transactionData!: TransactionData;
  cardAuthorizationRes!: CardAuthorizationResponse;
}
