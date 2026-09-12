import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";
import { AccountCardsResponse } from "@/dtos/bank-card/owned-cards.response";
import { onCardContentTabHandler } from "@/service/handler/onCardContentTabHandler";
import { useEffect, useState } from "react";

// constant to define how many different filtered BasicCardInfoResponses there are
const MAX_FILTERS = 2;

async function getAllCardsOwnedByAccount(): Promise<BasicCardInfo[]> {
    console.log("HELLO");
    const data: AccountCardsResponse = await onCardContentTabHandler();
    console.log(`data: ${data}`);
    console.log(`basic card info array: ${data.cards}`)
    return data.cards;
}

function filter(data: BasicCardInfo[]): BasicCardInfo[][] {
    const filteredCards: BasicCardInfo[][] = Array.from(
        { length: MAX_FILTERS },
        () => []
    );
    for (const card of data) {
        if (card.cardStatus.toLocaleUpperCase() === "ACTIVE") {
            filteredCards[0].push(card);
            continue;
        }

        if (card.cardStatus.toLocaleUpperCase() === "ISSUED") {
            filteredCards[1].push(card);
            continue;
        }
    }
    return filteredCards;
}

// Add a lot of things after basic features work
// such as, frozen cards, order by most used, order by balance, etc
export function useCardManager() {
    const [activeCards, setActiveCards] = useState<BasicCardInfo[]>([]);
    // Cards that need activation
    const [issuedCards, setIssuedCards] = useState<BasicCardInfo[]>([]);

    useEffect(() => {
        const loadCardsAndFilter = async () => {
            const data: BasicCardInfo[] = await getAllCardsOwnedByAccount();
            const filteredData: BasicCardInfo[][] = filter(data);

            setActiveCards(filteredData[0]);
            setIssuedCards(filteredData[1]);
        }

        loadCardsAndFilter();
    }, []);

    const addToIssuedCards = (card: BasicCardInfo) =>
        setIssuedCards(prevCards => [...prevCards, card]);

    const removeFromIssuedCards = (card: BasicCardInfo) =>
        setIssuedCards(prevCards => prevCards.filter(currCard => currCard.lastFour !== card.lastFour));

    const addToActiveCards = (card: BasicCardInfo) =>
        setActiveCards(prevCards => [...prevCards, card]);

    const removeFromActiveCards = (card: BasicCardInfo) =>
        setActiveCards(prevCards => prevCards.filter(currCard => currCard.lastFour !== card.lastFour));

    // at the moment, live-render changes happen when tab is clicked
    return { 
        activeCards, issuedCards, 
        addToIssuedCards, removeFromIssuedCards, 
        addToActiveCards, removeFromActiveCards 
    };
}