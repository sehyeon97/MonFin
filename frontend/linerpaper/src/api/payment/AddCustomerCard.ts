import type { AddPaymentMethodRequest } from "../../dto/processor/AddPaymentMethodRequest";
import { Url } from "../Url";

export async function AddCustomerCard(
  req: AddPaymentMethodRequest,
): Promise<void> {
  const endpoint =
    Url.Base + Url.PaymentProcessor + Url.CustomerAddPaymentMethod;
  await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(req),
  });
}
