import type { ProductRequest } from "../../dto/merchant/ProductRequest";
import type { ProductResponse } from "../../dto/merchant/ProductResponse";
import { Url } from "../Url";

export async function AddProductForMerchant(req: ProductRequest) {
  const endpoint = `${Url.Base}${Url.Merchant}${Url.MerchantAddProduct}`;
  const response: Response = await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(req),
  });

  if (response.ok) {
    const data: ProductResponse = await response.json();
    return data;
  }

  return null;
}
