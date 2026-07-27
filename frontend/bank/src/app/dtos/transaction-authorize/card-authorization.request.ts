// Must be an array of this DTO
export class CardAuthorizationRequest {
  transactionID!: string; // uuid
  customerID!: string; // uuid
  cardToken!: string;
  merchantID!: string; // uuid
  merchantName!: string; // aka businessName
  brand!: string;
  productName!: string;
  timestamp!: string;
  amount!: number;
  cryptogram!: string;
  redirectUrl!: string;
  serverUrl!: string;
}
