// Bank's CardAuthorizationRequest requires these props:
// UUID transactionID, String cardToken, UUID merchantID, String merchantName,
// Instant timestamp, int amount, String cryptogram, String callbackUrl
export class TransactionDetailsRequest {
    transactionID!: string;
    customerID!: string;
    cardToken!: string;
    merchantID!: string;
    merchantName!: string; // businessName
    brand!: string;
    productName!: string;
    timestamp!: string;
    amount!: number;
    cryptogram!: string;
    redirectUrl!: string;
    serverUrl!: string;
}
