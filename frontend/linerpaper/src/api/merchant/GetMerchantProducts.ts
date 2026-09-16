import type { AllProductsRequest } from "../../dto/merchant/AllProductsRequest";
import type { AllProductsResponse } from "../../dto/merchant/AllProductsResponse";
import { UserTypes, type UserType } from "../../types/UserType";
import { Url } from "../Url";

export async function GetMerchantProducts(
  role: UserType,
  req?: AllProductsRequest,
): Promise<AllProductsResponse> {
  const endpoint = `${Url.Base}${Url.Merchant}${Url.MerchantProductPage}`;

  let response: Response;

  if (role === UserTypes.Merchant) {
    response = await fetch(endpoint, {
      method: "GET",
      credentials: "include",
    });
  } else {
    response = await fetch(`${endpoint}?businessName=${req!.businessName}`, {
      method: "GET",
      credentials: "include",
    });
  }

  if (response.ok) {
    const data: AllProductsResponse = await response.json();
    return data;
  }

  throw new Error("Unable to retrieve merchant's products");
}
