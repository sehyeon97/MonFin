import { useNavigate } from "react-router-dom";
import { CardDetailsForm } from "../../components/forms/CardDetails/CardDetailsForm";
import { TokenizeCard } from "../../api/tsp/TokenizeCard";
import type { CardTokenizationRequest } from "../../dto/tsp/CardTokenizationRequest";
import { useState } from "react";
import { AddCustomerCard } from "../../api/payment/AddCustomerCard";

export function SaveCardPage() {
    const [message, setMessage] = useState("");

    const navigate = useNavigate();

    async function attemptCardTokenization(request: CardTokenizationRequest) {
        const response = await TokenizeCard(request);
        
        const tokenized: boolean = response.tokenized;
        const message: string = response.message;
        const cardToken: string = response.cardToken;

        if (!tokenized) {
            setMessage("Card information is incorrect");
            return;
        }

        // save card information to payment processor backend
        await AddCustomerCard({
            cardToken: cardToken,
            lastFour: request.pan.substring(12),
            fullName: request.fullName,
            network: "VISA", // not implemented yet
            expMonth: Number(request.expMonth),
            expYear: Number(request.expYear),
        })

        // after a second of displaying message,
        // navigate user to their saved cards page
        setMessage(message);
        setTimeout(() => {
            navigate("/view-saved-cards");
        }, 1000);
    }

    return (
        <div>
            <title>Save new card to my profile</title>
            <CardDetailsForm onSubmit={attemptCardTokenization} />
            <h3 hidden={message === "" ? true : false}>{message}</h3>
        </div>
    );
}