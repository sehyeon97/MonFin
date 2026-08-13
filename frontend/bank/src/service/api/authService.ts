"use-client";

import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";

export async function signup(
  request: CreateBankAccountRequest,
): Promise<Response> {
  const endpoint: string =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_REGISTER_ACCOUNT}`;
  return await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}

export async function login(request: LoginUserCredentials): Promise<Response> {
  const endpoint: string =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_LOGIN_USER}`;
  return await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}

export async function validateReturningUser(): Promise<Response> {
  const endpoint: string =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_IS_JWT_VALID_STILL}`;
  return await fetch(endpoint, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
  });
}
