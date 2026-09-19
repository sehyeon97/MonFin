import type { AllProductsResponse } from "../../dto/merchant/AllProductsResponse";
import { UserTypes, type UserType } from "../../types/UserType";
import { Url } from "../Url";

interface MerchantName {
  businessName?: string;
}

export async function GetMerchantProducts(
  role: UserType,
  { businessName }: MerchantName,
): Promise<AllProductsResponse> {
  const merchantEndpoint = `${Url.Base}${Url.Merchant}${Url.MerchantProductPage}`;
  const customerEndpoint = `${Url.Base}${Url.Merchant}${Url.CustomerViewMerchantProducts}`;

  let response: Response;

  if (role === UserTypes.Merchant) {
    response = await fetch(merchantEndpoint, {
      method: "GET",
      credentials: "include",
    });
  } else {
    response = await fetch(`${customerEndpoint}?businessName=${businessName}`, {
      method: "GET",
      credentials: "include",
    });
  }

  if (response.ok) {
    const data: AllProductsResponse = await response.json();
    console.log("DATA FETCHED FOR CUSTOMER: ", data.products);
    return data;
  }

  throw new Error("Unable to retrieve merchant's products");
}
