import type { CardTokenizationRequest } from "../../dto/tsp/CardTokenizationRequest";
import type { CardTokenizationResponse } from "../../dto/tsp/CardTokenizationResponse";
import { Url } from "../Url";

export async function TokenizeCard(
  req: CardTokenizationRequest,
): Promise<CardTokenizationResponse> {
  return await fetch(Url.TSP, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(req),
  })
    .then((response) => response.json())
    .then((data) => {
      return data;
    });
}
