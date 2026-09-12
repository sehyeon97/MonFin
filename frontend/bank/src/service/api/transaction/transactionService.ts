import {
  domain,
  transactionGateway,
  transactionHistoryEndpoint,
} from "@/util/backend-endpoints";

const endpoint = `${domain} ${transactionGateway} ${transactionHistoryEndpoint}`;

export async function getTransactionHistory(): Promise<Response> {
  return await fetch(endpoint, {
    method: "GET",
    credentials: "include",
  });
}
