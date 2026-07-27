import { TransactionData } from "../transaction-authorize/transaction-response/transaction-data.response";

export class VerifyOTPRequest {
  otpID!: string;
  otp!: string;
  metaData!: TransactionData;
}
