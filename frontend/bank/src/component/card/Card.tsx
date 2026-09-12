import { BasicCardInfo } from "@/dtos/bank-card/basic-card-info";
import { Column } from "../content/col/Column";
import { Row } from "../content/row/Row";
import { CARD_CLASS_NAME, CARD_EXP_DATE_CLASS_NAME, CARD_LAST_FOUR_CLASS_NAME, CARD_NETWORK_CLASS_NAME, CARD_TIER_CLASS_NAME, CARD_TYPE_CLASS_NAME } from "@/style/classnames";

/**
 * Debit cards, credit cards, they all display the same thing:
 * 1. Last 4 digits of PAN
 * 2. Cardholder's full name
 * 3. Expire month/year
 * 4. Type of card (Credit | Debit)
 * 5. Card Network (Visa | Mastercard | Discover)
 * Additionally, Card Tier (Bronze | Silver | Gold)
 */
type CardProps = {
    details: BasicCardInfo;
};

// How the card visually looks
// It doesn't need all the parts to a card, such as full name, card status, etc
export function Card({
    details,
}: CardProps) {
    console.log(`LAST FOUR: ${details.lastFour}`)
    return (
        <div className={CARD_CLASS_NAME}>
            {/* Top */}
            <Row alignment="between">
                <span className={CARD_NETWORK_CLASS_NAME}>
                    {details.cardNetwork}
                </span>

                <span className={CARD_TYPE_CLASS_NAME}>
                    {details.cardType}
                </span>
            </Row>

            {/* Middle */}
            <Column alignment="center">
                <span className={CARD_LAST_FOUR_CLASS_NAME}>
                    •••• {details.lastFour}
                </span>
            </Column>

            {/* Bottom */}
            <Row alignment="between">
                <p className={CARD_EXP_DATE_CLASS_NAME}>
                    {String(details.expMonth).padStart(2, "0")}/{String(details.expYear).slice(-2)}
                </p>

                <span className={CARD_TIER_CLASS_NAME}>
                    {details.cardTier}
                </span>
            </Row>
        </div>
    );
}