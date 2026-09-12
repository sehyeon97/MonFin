import { NewCardRequest } from "@/dtos/bank-card/new-card.request";
import { activateCard, createCard } from "../api/card/newCardService";
import { DeleteCardRequest } from "@/dtos/bank-card/delete-card.request";
import { removeCard } from "../api/card/removeCardService";
import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";

export async function createCardForAccount(request: NewCardRequest) {
  const response: Response = await createCard(request);

  // at the moment, only happy path is tested
  // Need Activation needs to show this newly added card
  if (response.ok) {
    return response.json();
  }

  return "Some error message.";
}

export async function removeCardFromAccount(req: DeleteCardRequest) {
  const response: Response = await removeCard(req);

  if (response.ok) {
    return "Success";
  }

  return "Error message";
}

export async function activateIssuedCard(req: BasicCardInfo) {
  const response: Response = await activateCard(req);

  // backend throws 403 if couldn't be activated
  if (response.status === 403) {
    throw new Error("Card could not be activated");
  }

  if (response.ok) {
    return req;
  }

  throw new Error("Something went wrong");
}
