export interface AddPaymentMethodRequest {
  cardToken: string;
  lastFour: string;
  fullName: string;
  network: string;
  expMonth: number;
  expYear: number;
}
