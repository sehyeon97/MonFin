import type { CustomerSavedPaymentMethodResponse } from "../../dto/processor/CustomerSavedPaymentMethodResponse";
import { Url } from "../Url";

export async function ViewCustomerCards(
  userID: string,
): Promise<CustomerSavedPaymentMethodResponse[]> {
  const endpoint =
    Url.Base +
    Url.PaymentProcessor +
    Url.CustomerViewSavedPaymentMethods +
    "/" +
    userID;

  return await fetch(endpoint, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((data) => {
      return data;
    });
}
