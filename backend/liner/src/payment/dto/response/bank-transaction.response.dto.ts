export class BankTransactionResponse {
    authorized!: boolean;
    authorizationCode!: string;
    declineReason!: string;
    bankCallbackUrl!: string;
}
