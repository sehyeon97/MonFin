import { Container } from "@/component/Container";
import { Content } from "../Content";
import { Row } from "../row/Row";
import { CONTENT_ROW_CHILD_CLASS_NAME } from "@/style/classnames";
import { useCardManager } from "@/hook/card-content/useCardManager";
import { Button } from "@/component/Button";
import { useState } from "react";
import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";
import { MuffinIcon } from "@/loading/icon/Muffin";
import { removeCardFromAccount } from "@/service/handler/removeCardHandler";
import { DeleteCardRequest } from "@/dtos/bank-card/delete-card.request";
import { ShowActiveAndActivateCards } from "./row/first/ShowActiveAndActivateCards";
import { AddCard } from "./row/second/AddCard";

export function CardManagementContent() {
    const { 
        activeCards, activateCards: issuedCards,
        addToIssuedCards, removeFromIssuedCards, 
        addToActiveCards, removeFromActiveCards 
    } = useCardManager();

    const [isLoadingDeleteCardRequest, setIsLoadingDeleteCardRequest] = useState(false);

    function activateCard(card: BasicCardInfo) {
        removeFromIssuedCards(card);
        addToActiveCards(card);
    }

    async function closeCard(card: BasicCardInfo) {
        setIsLoadingDeleteCardRequest(true);
        removeFromActiveCards(card);
        const request: DeleteCardRequest = {
            lastFour: card.lastFour,
        }

        try {
            await removeCardFromAccount(request);
        } finally {
            setIsLoadingDeleteCardRequest(false);
        }
    }

    return (
        <Content className="space-y-5">
            {/* First Row: View Active and Issued Cards */}
            <ShowActiveAndActivateCards
                activeCards={activeCards}
                issuedCards={issuedCards}
                activateCard={activateCard}
            />

            {/* Second Row: Add Card */}
            <AddCard addToIssuedCards={addToIssuedCards} />

            {/* Close Card, View Frozen Cards */}
            <Row>
                <Container
                    title="Close Card"
                    className={`${CONTENT_ROW_CHILD_CLASS_NAME} justify-evenly w-[22%]`}
                >
                    {activeCards.map((card, index) => (
                        <div key={index}>
                            <Button
                                key={card.lastFour}
                                text={card.lastFour}
                                onClick={() => closeCard(card)}
                                disabled={isLoadingDeleteCardRequest}
                                loadingIcon={<MuffinIcon />}
                            />
                        </div>
                    ))}
                    {activeCards.length < 1 && <p>No data to display</p>}
                </Container>
            </Row>
        </Content>
    );
}