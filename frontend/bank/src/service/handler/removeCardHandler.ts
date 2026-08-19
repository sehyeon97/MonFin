import { DeleteCardRequest } from "@/dtos/bank-card/delete-card.request";
import { removeCard } from "../api/removeCardService";

export async function removeCardFromAccount(req: DeleteCardRequest) {
  const response: Response = await removeCard(req);

  if (response.ok) {
    return "Success";
  }

  return "Error message";
}
