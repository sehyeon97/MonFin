export class CardAuthorizationResponse {
  authorized!: boolean;
  authorizationCode!: string;
  declineReason!: string;
  url!: string; // only when bank needs otp verification
  otpID!: string;
}
