import { CreateBankAccountRequest } from "@/app/dtos/bank-account/create-bank-account.request";
import { LoginUserCredentialsResponse } from "@/app/dtos/bank-account/login.user-credentials.response";

export async function createBankAccount(
  request: CreateBankAccountRequest,
): Promise<LoginUserCredentialsResponse> {
  const env = process.env;
  const endpoint: string = env.BANK_CONTROLLER_CREATE_BANK_ACCOUNT!;

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  // atm, fetched results always return an ok status
  // later I need to return strings based on error statuses
  return (await response.json()) as LoginUserCredentialsResponse;
}
