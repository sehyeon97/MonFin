export class CardAuthorizationResponse {
    authorized!: boolean;
    authorizationCode!: string;
    declineReason!: string;
    bankCallbackUrl!: string;
}
