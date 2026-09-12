import { useState } from "react";

import { Button } from "@/component/Button";
import { Container } from "@/component/Container";
import { Row } from "@/component/content/row/Row";
import { CardNetwork } from "@/hook/card-content/types/CardNetwork";
import { CardTier } from "@/hook/card-content/types/CardTier";
import { useCardSelection } from "@/hook/card-content/useCardSelection";
import { MuffinIcon } from "@/loading/icon/Muffin";
import { CONTENT_ROW_CHILD_CLASS_NAME } from "@/style/classnames";
import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";
import { NewCardRequest } from "@/dtos/bank-card/new-card.request";
import { createCardForAccount } from "@/service/handler/cardHandler";

type AddCardProps = {
    addToIssuedCards: (card: BasicCardInfo) => void;
}

export function AddCard({ addToIssuedCards: addToActivateCards }: AddCardProps) {
    // used when selecting a card to add to bank account
    const { cardSelection, setCardTier, setCardNetwork, setCardType } = useCardSelection();
    const [isLoadingNewCardRequest, setIsLoadingNewCardRequest] = useState(false);

    function onTierButtonClick(tier: CardTier) {
        setCardTier(tier);
    }

    function onNetworkButtonClick(network: CardNetwork) {
        setCardNetwork(network);
    }

    function onTypeButtonClick(type: string) {
        setCardType(type);
    }

    async function onSubmitNewCardRequest() {
        console.log("IS BEING SUBMITTED?");
        setIsLoadingNewCardRequest(true);
        const request: NewCardRequest = {
            cardType: cardSelection.type,
            cardNetwork: cardSelection.network,
            cardTier: cardSelection.tier,
        }

        try {
            const card: BasicCardInfo = await createCardForAccount(request);
            addToActivateCards(card);
        } finally {
            setIsLoadingNewCardRequest(false);
        }
    }

    return (
        <Row alignment="center">
            <Container
                title="Add Card"
                className={`${CONTENT_ROW_CHILD_CLASS_NAME} justify-evenly`}
            >
                {/* Matches NewCardRequest */}
                <Row>
                    <Container title="Card Tier">
                        <Row>
                            <Button
                                text="Bronze"
                                onClick={() => onTierButtonClick(CardTier.BRONZE)}
                                isSelected={cardSelection.tier === CardTier.BRONZE}
                            ></Button>
                            <Button
                                text="Silver"
                                onClick={() => onTierButtonClick(CardTier.SILVER)}
                                isSelected={cardSelection.tier === CardTier.SILVER}
                            ></Button>
                            <Button
                                text="Gold"
                                onClick={() => onTierButtonClick(CardTier.GOLD)}
                                isSelected={cardSelection.tier === CardTier.GOLD}
                            ></Button>
                        </Row>
                    </Container>
                    <Container title="Card Network">
                        <Row>
                            <Button
                                text="VISA"
                                onClick={() => onNetworkButtonClick(CardNetwork.VISA)}
                                isSelected={cardSelection.network === CardNetwork.VISA}
                            ></Button>
                            <Button
                                text="MasterCard"
                                onClick={() => onNetworkButtonClick(CardNetwork.MASTERCARD)}
                                isSelected={cardSelection.network === CardNetwork.MASTERCARD}
                            ></Button>
                            <Button
                                text="Discover"
                                onClick={() => onNetworkButtonClick(CardNetwork.DISCOVER)}
                                isSelected={cardSelection.network === CardNetwork.DISCOVER}
                            ></Button>
                        </Row>
                    </Container>
                    <Container title="Card Type">
                        <Row>
                            <Button
                                text="DEBIT"
                                onClick={() => onTypeButtonClick("DEBIT")}
                                isSelected={cardSelection.type === "DEBIT"}
                            ></Button>
                            <Button
                                text="CREDIT"
                                onClick={() => onTypeButtonClick("CREDIT")}
                                isSelected={cardSelection.type === "CREDIT"}
                            ></Button>
                        </Row>
                    </Container>
                    <Button
                        text="Submit"
                        onClick={onSubmitNewCardRequest}
                        disabled={isLoadingNewCardRequest}
                        loadingIcon={<MuffinIcon />}
                        isSelected={isLoadingNewCardRequest}
                    />
                </Row>
            </Container>
        </Row>
    );
}