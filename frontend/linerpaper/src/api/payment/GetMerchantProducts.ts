import type { ProductsResponse } from "../../dto/merchant/AllProductsResponse";
import { Url } from "../Url";

export async function GetMerchantProducts(merchantID: string) {
  const endpoint = `${Url.Base}${Url.Merchant}${Url.MerchantProductPage}?merchantID=${merchantID}`;
  const response: Response = await fetch(endpoint, {
    method: "GET",
    credentials: "include",
  });

  if (response.ok) {
    const data: ProductsResponse = await response.json();
    return data.products;
  }

  return [];
}
