import { Container } from "../Container";

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
    lastFour: string;
    fullName: string;
    expireMonth: string;
    expireYear: string;
    cardType: string;
    cardNetwork: string;
    cardTier: string;
};

export function Card({
    lastFour, fullName, expireMonth, expireYear, cardType, cardNetwork, cardTier
}: CardProps) {
    return (
        <Container title={cardTier}>
            <p></p>
        </Container>
    );
}