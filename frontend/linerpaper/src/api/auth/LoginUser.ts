import type { LoginRequest } from "../../dto/user/LoginRequest";
import { UserTypes } from "../../types/UserType";
import { Url } from "../Url";

export async function LoginUser(
  request: LoginRequest,
  userType: string,
): Promise<string> {
  const userUrl = userType === UserTypes.Customer ? Url.Customer : Url.Merchant;
  return await fetch(Url.Base + userUrl + Url.Login, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  })
    .then((response) => response.text())
    .then((data) => {
      return data;
    });
}
