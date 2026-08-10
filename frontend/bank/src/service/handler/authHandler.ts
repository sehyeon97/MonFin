"use-client";

import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";
import { login, signup } from "../api/authService";

/**
 * In case we want to do something with the different response (entities) in the future
 */
export async function loginUser(request: LoginUserCredentials) {
  const response: Response = await login(request);

  if (response.ok) {
    return "";
  }
  return response.text();
}

export async function signupUser(request: CreateBankAccountRequest) {
  const response: Response = await signup(request);

  if (response.ok) {
    return "";
  }
  return response.text();
}
