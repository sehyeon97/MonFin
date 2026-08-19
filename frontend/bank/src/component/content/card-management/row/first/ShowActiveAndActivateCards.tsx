import { Button } from "@/component/Button"
import { Card } from "@/component/card/Card"
import { Container } from "@/component/Container"
import { Column } from "@/component/content/col/Column"
import { Row } from "@/component/content/row/Row"
import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info"
import { CONTENT_ROW_CHILD_CLASS_NAME } from "@/style/classnames"

type CardsProps = {
    activeCards: BasicCardInfo[];
    issuedCards: BasicCardInfo[];
    activateCard: (card: BasicCardInfo) => void;
}

/* View Active and Issued Cards */
export function ShowActiveAndActivateCards({ activeCards, issuedCards, activateCard }: CardsProps) {
    return (
        <Row alignment="evenly">
            <Container
                title="Active Cards"
                className={`${CONTENT_ROW_CHILD_CLASS_NAME} justify-evenly w-[45%]`}
            >
                {activeCards.map((card, index) => (
                    <div key={index}>
                        <Card details={card} />
                    </div>
                ))}
                {activeCards.length < 1 && <p>No data to display</p>}
            </Container>

            <Container
                title="Issued Cards"
                className={`${CONTENT_ROW_CHILD_CLASS_NAME} justify-evenly w-[45%]`}
            >
                {issuedCards.map((card, index) => (
                    <div key={index}>
                        <Column>
                            <Card
                                details={card}
                                activateCard={(card: BasicCardInfo) => activateCard(card)}
                            />
                            <Button
                                text="Activate"
                                className="bg-slate-800 py-2!"
                            />
                        </Column>
                    </div>
                ))}
                {issuedCards.length < 1 && <p>No data to display</p>}
            </Container>
        </Row>
    );
}