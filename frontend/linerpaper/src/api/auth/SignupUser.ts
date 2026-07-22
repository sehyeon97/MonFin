import type { SignupRequest } from "../../dto/user/SignupRequest";
import { UserTypes } from "../../types/UserType";
import { Url } from "../Url";

export async function SignupUser(request: SignupRequest, userType: string) {
  const userUrl = userType === UserTypes.Customer ? Url.Customer : Url.Merchant;
  return await fetch(Url.Base + userUrl + Url.Signup, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  })
    .then((response) => response.json())
    .then((data) => {
      return data;
    });
}
