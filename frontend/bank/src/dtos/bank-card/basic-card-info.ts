import { CardNetwork } from "@/hook/card-content/types/CardNetwork";
import { CardStatus } from "@/hook/card-content/types/CardStatus";
import { CardTier } from "@/hook/card-content/types/CardTier";

export class BasicCardInfo {
  lastFour!: string;
  expMonth!: string;
  expYear!: string;
  cardTier!: CardTier;
  cardNetwork!: CardNetwork;
  cardType!: string;
  cardStatus!: CardStatus;
  monthlyLimit!: number;
  dailyLimit!: number;
  availableCredit!: number;
  balance!: number;
  isDebit!: boolean;
}
