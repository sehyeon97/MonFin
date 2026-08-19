import { useState } from "react";

import { CardTier } from "./types/CardTier";
import { CardNetwork } from "./types/CardNetwork";

export function useCardSelection() {
    const [cardSelection, setCardSelection] = useState({
        tier: CardTier.BRONZE,
        network: CardNetwork.VISA,
        type: "DEBIT",
    });

    const setCardTier = (tier: CardTier) =>
        setCardSelection(prevTier => ({ ...prevTier, tier }));

    const setCardNetwork = (network: CardNetwork) =>
        setCardSelection(prevNetwork => ({ ...prevNetwork, network }));

    const setCardType = (type: string) =>
        setCardSelection(prevType => ({ ...prevType, type }));

    return { cardSelection, setCardTier, setCardNetwork, setCardType };
}