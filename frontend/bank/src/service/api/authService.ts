"use-client";

import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";

export async function signup(
  request: CreateBankAccountRequest,
): Promise<Response> {
  return await fetch(
    process.env.BANK_AUTHENTICATION_CONTROLLER! +
      process.env.BANK_AUTHENTICATION_REGISTER_ACCOUNT,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(request),
    },
  );
}

export async function login(request: LoginUserCredentials): Promise<Response> {
  return await fetch(
    process.env.BANK_AUTHENTICATION_CONTROLLER! +
      process.env.BANK_AUTHENTICATION_LOGIN_USER!,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(request),
    },
  );
}
