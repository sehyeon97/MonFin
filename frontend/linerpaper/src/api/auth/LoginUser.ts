import type { LoginRequest } from "../../dto/user/LoginRequest";
import { Url } from "../Url";

export async function LoginUser(request: LoginRequest): Promise<string> {
  const response = await fetch(Url.Base + Url.AuthLogin, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });

  console.log("email: " + request.email);
  console.log("password: " + request.password);
  console.log("user role: " + request.role);

  if (!response.ok) {
    return "Invalid email or password.";
  }

  const data = await response.text();
  return data;
}
