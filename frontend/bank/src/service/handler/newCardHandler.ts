import { NewCardRequest } from "@/dtos/bank-card/new-card.request";
import { createCard } from "../api/newCardService";

export async function createCardForAccount(request: NewCardRequest) {
  const response: Response = await createCard(request);

  // at the moment, only happy path is tested
  // Need Activation needs to show this newly added card
  if (response.ok) {
    return response.json();
  }

  return "Some error message.";
}
