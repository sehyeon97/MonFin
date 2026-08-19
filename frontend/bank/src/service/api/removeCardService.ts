import { DeleteCardRequest } from "@/dtos/bank-card/delete-card.request";

export async function removeCard(
  request: DeleteCardRequest,
): Promise<Response> {
  const endpoint: string =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_DELETE_CARD}`;

  return await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}
