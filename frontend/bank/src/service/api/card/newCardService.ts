import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";
import { NewCardRequest } from "@/dtos/bank-card/new-card.request";
import {
  activateCardEndpoint,
  bankGateway,
  createCardEndpoint,
  domain,
} from "@/util/backend-endpoints";

const createCardUrl = `${domain} ${bankGateway} ${createCardEndpoint}`;
const activateCardUrl = `${domain} ${bankGateway} ${activateCardEndpoint}`;

export async function createCard(request: NewCardRequest): Promise<Response> {
  return await fetch(createCardUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}

export async function activateCard(request: BasicCardInfo): Promise<Response> {
  return await fetch(activateCardUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(request),
  });
}
