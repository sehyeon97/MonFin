export class BankTransactionResponse {
    transactionID!: string;
    authorized!: boolean;
    authorizationCode!: string;
    declineReason!: string;
    bankCallbackUrl!: string;
}
