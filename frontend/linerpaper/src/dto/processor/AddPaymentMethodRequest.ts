export interface AddPaymentMethodRequest {
  customerID: string;
  cardToken: string;
  lastFour: string;
  fullName: string;
  network: string;
  expMonth: number;
  expYear: number;
}
