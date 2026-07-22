export interface SignupRequest {
  email: string;
  password: string;
  verified: boolean;
  billingAddress: string;
  billingCity: string;
  billingState: string;
  billingZip: string;
}
