import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";
import {
  login,
  logoutAndRemoveCredentials,
  refreshAccessToken,
  signup,
} from "../api/authService";

/**
 * In case we want to do something with the different response (entities) in the future
 * Returns list of cards owned by account on success
 */
export async function loginUser(request: LoginUserCredentials) {
  const response: Response = await login(request);

  if (response.ok) {
    return "Success";
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

export async function refreshUserSession(): Promise<string> {
  const response: Response = await refreshAccessToken();

  if (response.ok) {
    return "Success";
  }

  throw new Error("Failed to refresh session");
}

export async function logoutUser(): Promise<string> {
  const response: Response = await logoutAndRemoveCredentials();

  if (response.status === 204) {
    return "SUCCESS";
  }

  throw new Error("Failed to logout user");
}
