// later, it will be changed so that the saving payment method form
// from TSP frontend would be embedded as an iframe
// because the payment processor shouldn't be able to directly call the TSP backend
export interface CardTokenizationRequest {
  pan: string;
  cvv: string;
  fullName: string;
  expMonth: string;
  expYear: string;
}
