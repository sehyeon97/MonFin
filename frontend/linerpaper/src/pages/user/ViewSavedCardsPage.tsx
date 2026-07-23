import { useEffect, useState } from "react";
import type { CustomerSavedPaymentMethodResponse } from "../../dto/processor/CustomerSavedPaymentMethodResponse";
import { ViewCustomerCards } from "../../api/payment/ViewCustomerCards";
import { Card } from "../../components/card/Card";

export function ViewSavedCardsPage() {
    const [savedCards, setSavedCards] = useState<CustomerSavedPaymentMethodResponse[]>([]);

    useEffect(() => {
        async function fetchCustomerSavedPaymentMethods(): Promise<void> {
            const data: CustomerSavedPaymentMethodResponse[] =  await ViewCustomerCards();
            setSavedCards(data);
        }

        fetchCustomerSavedPaymentMethods();
    }, []);

    if (savedCards.length < 1) {
        return (
            <div>
                <h2>Add a card to see something here!</h2>
            </div>
        );
    }

    return (
        <div>
            {savedCards.map((savedPaymentMethod: CustomerSavedPaymentMethodResponse) => (
                <Card
                    network={savedPaymentMethod.network}
                    lastFour={savedPaymentMethod.lastFour}
                    expMonth={savedPaymentMethod.expMonth}
                    expYear={savedPaymentMethod.expYear}
                />
            ))}
        </div>
    );
}