import type { SignupRequest } from "../../dto/user/SignupRequest";
import { UserTypes } from "../../types/UserType";
import { Url } from "../Url";

export async function SignupUser(request: SignupRequest, userType: string) {
  const userUrl = userType === UserTypes.Customer ? Url.Customer : Url.Merchant;

  console.log("userType:", userType);
  console.log("Customer:", UserTypes.Customer);
  console.log("Merchant:", UserTypes.Merchant);

  const response: Response = await fetch(Url.Base + userUrl + Url.Signup, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (response.ok) {
    return response.text();
  }

  console.log(response.status);
  return "";
}
