import type { ProductResponse } from "../../dto/merchant/ProductResponse";
import type { UpdateProductRequest } from "../../dto/merchant/UpdateProductRequest";
import { Url } from "../Url";

export async function UpdateProductForMerchant(req: UpdateProductRequest) {
  const endpoint = `${Url.Base}${Url.Merchant}${Url.MerchantUpdateProduct}`;

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

  throw new Error("Something went wrong while attempting to update product");
}
