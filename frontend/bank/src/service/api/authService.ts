import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";

const domain: string = `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}`;
const controller: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_CONTROLLER}`;

const registerEndpoint: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_REGISTER_ACCOUNT}`;
const loginEndpoint: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_LOGIN_USER}`;
const validateEndpoint: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_IS_JWT_VALID_STILL}`;
const refreshEndpoint: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_REFRESH_ACCESS_TOKEN}`;
const logoutEndpoint: string = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_LOGOUT}`;

export async function signup(
  request: CreateBankAccountRequest,
): Promise<Response> {
  const endpoint: string = `${domain}${controller}${registerEndpoint}`;
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
  const endpoint: string = `${domain}${controller}${loginEndpoint}`;
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
  const endpoint: string = `${domain}${controller}${validateEndpoint}`;
  return await fetch(endpoint, {
    method: "GET",
    credentials: "include",
  });
}

export async function refreshAccessToken() {
  const endpoint: string = `${domain}${controller}${refreshEndpoint}`;
  return await fetch(endpoint, {
    method: "POST",
    credentials: "include",
  });
}

export async function logoutAndRemoveCredentials(): Promise<Response> {
  const endpoint: string = `${domain}${controller}${logoutEndpoint}`;
  return await fetch(endpoint, {
    method: "POST",
    credentials: "include",
  });
}
