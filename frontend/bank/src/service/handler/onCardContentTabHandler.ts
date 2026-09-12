import { AccountCardsResponse } from "@/dtos/bank-card/owned-cards.response";
import { getAllCards } from "../api/card/getCardsService";

export async function onCardContentTabHandler(): Promise<AccountCardsResponse> {
  const response: Response = await getAllCards();

  if (response.ok) {
    const data: AccountCardsResponse = await response.json();

    if (data) {
      // console.log(`DATA: ${data}`);
      // console.log(`LAST FOUR FRONTEND: ${data.cards[0]?.lastFour}`);
      return data;
    }

    return {
      cards: [],
    };
  }

  if (response.status === 403) {
    throw new Error(`Failed to get cards: ${response.status}`);
  }

  // this should be some error handling later for different response statuses
  return {
    cards: [],
  };
}
