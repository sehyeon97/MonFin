import { useEffect, useState } from "react";
import type { CustomerSavedPaymentMethodResponse } from "../../dto/processor/CustomerSavedPaymentMethodResponse";
import { ViewCustomerCards } from "../../api/payment/ViewCustomerCards";
import { Card } from "../../components/card/Card";

export function ViewSavedCardsPage() {
    const [savedCards, setSavedCards] = useState<CustomerSavedPaymentMethodResponse[]>([]);

    // this is bad that I need to constantly retrieve from localStorage
    // change it into a provider - consumer dynamic later
    const userID: string = window.localStorage.getItem("userID")!;

    // it's like a constructor. runs once when page is loaded indicated by [] at the end
    // however, atm it will change whenever userID is changed
    // (but in reality it runs once anyway which is why I need to make the above change)
    useEffect(() => {
        async function fetchCustomerSavedPaymentMethods(): Promise<void> {
            const data: CustomerSavedPaymentMethodResponse[] =  await ViewCustomerCards(userID);
            setSavedCards(data);
        }

        fetchCustomerSavedPaymentMethods();
    }, [userID]);

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