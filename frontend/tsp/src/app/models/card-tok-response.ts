export interface CardTokenizationResponse {
  tokenized: boolean;
  message: string;

  cardToken: string;
  lastFour: string;
  fullName: string;
  network: string;
  expMonth: number;
  expYear: number;
}
