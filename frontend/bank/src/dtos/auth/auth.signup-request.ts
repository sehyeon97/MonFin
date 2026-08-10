import { LoginUserCredentials } from "./auth.login-request";

export interface CreateBankAccountRequest extends LoginUserCredentials {
  fullName: string;
  phoneNumber: string;
}
