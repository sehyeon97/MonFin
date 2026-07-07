export class TransactionResponse {
    status!: string; // approved or declined
    bankCallbackUrl!: string; // when otp is required
    transactionID!: string;
}
