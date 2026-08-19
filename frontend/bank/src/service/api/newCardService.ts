import { NewCardRequest } from "@/dtos/bank-card/new-card.request";

export async function createCard(request: NewCardRequest): Promise<Response> {
  const endpoint =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_CONTROLLER_CREATE_CARD}`;

  return await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}
