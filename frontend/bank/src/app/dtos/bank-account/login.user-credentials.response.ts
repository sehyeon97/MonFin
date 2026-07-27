import { LoginErrorResponse } from "./login-error.response";
import { SignupErrorResponse } from "./signup-error.response";

export class LoginUserCredentialsResponse {
  jwt!: string; // atm it's bank account id, but should be jwt later
  signupError!: SignupErrorResponse;
  loginError!: LoginErrorResponse;
}
