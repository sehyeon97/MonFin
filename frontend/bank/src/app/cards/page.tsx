import { Card } from "@/component/card/Card";

/**
 * View all cards owned by bank account
 */
export default function Cards() {
    return (
        <Card 
            lastFour="1234" 
            fullName="pooding" 
            expireMonth="12" expireYear="2026" 
            cardType="DEBIT" cardNetwork="VISA" cardTier="GOLD"
        >
            
        </Card>
    );
}