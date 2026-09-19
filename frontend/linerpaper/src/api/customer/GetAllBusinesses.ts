import type { MerchantAndProducts } from "../../dto/customer/MerchantAndProducts";
import { Url } from "../Url";

export async function GetAllBusinesses() {
  const endpoint = `${Url.Base}${Url.Merchant}${Url.CustomerViewMerchantProducts}`;

  const response: Response = await fetch(endpoint, {
    method: "GET",
  });

  if (response.ok) {
    return (await response.json()) as MerchantAndProducts;
  }

  throw new Error("Could not retrieve businesses from database");
}
