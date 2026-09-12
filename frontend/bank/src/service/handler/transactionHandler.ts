import { getTransactionHistory } from "../api/transaction/transactionService";

export async function getTransactions() {
  const response: Response = await getTransactionHistory();

  if (response.ok) {
    return response.json();
  }

  throw new Error("Could not retrieve transactions");
}
