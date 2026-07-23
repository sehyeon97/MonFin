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
    credentials: "include",
    body: JSON.stringify(request),
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("email: " + data.email);
      console.log("password: " + data.password);
      console.log("verified: " + data.verified);
      console.log("billing address: " + data.billingAddress);
      console.log("billing city: " + data.billingCity);
      console.log("state: " + data.billingState);
      console.log("zip: " + data.billingZip);
      return data;
    });
}
