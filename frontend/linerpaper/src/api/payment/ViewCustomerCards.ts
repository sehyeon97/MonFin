import type { CustomerSavedPaymentMethodResponse } from "../../dto/processor/CustomerSavedPaymentMethodResponse";
import { Url } from "../Url";

export async function ViewCustomerCards(): Promise<
  CustomerSavedPaymentMethodResponse[]
> {
  const endpoint =
    Url.Base + Url.PaymentProcessor + Url.CustomerViewSavedPaymentMethods;

  return await fetch(endpoint, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("customer card list: " + data);
      return data;
    });
}
