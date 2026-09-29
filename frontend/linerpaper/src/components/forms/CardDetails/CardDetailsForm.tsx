import { useEffect, useState } from "react";
import type { CardTokenizationResponse } from "../../../dto/tsp/CardTokenizationResponse";
import { AddCustomerCard } from "../../../api/payment/AddCustomerCard";
import { useNavigate } from "react-router-dom";

export function CardDetailsForm() {
    const [message, setMessage] = useState("");
    const [haveAddedCard, setHaveAddedCard] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        async function handleIframeMessage(event: MessageEvent) {
            // Verify the message is sent by bank/tsp frontend
            if (event.origin !== "http://localhost:4200") {
                return;
            }

            // we know the response dto is the same across our clients / servers
            if (!event.data.tokenized) {
                setMessage("Could not add this card.\nPlease check card information and try again.");
                return;
            }

            console.log("cardToken: ", event.data.cardToken);
            console.log("lastFour: ", event.data.lastFour);
            console.log("fullName: ", event.data.fullName);
            console.log("network: ", event.data.network);
            console.log("expMonth: ", event.data.expMonth);
            console.log("expYear: ", event.data.expYear);

            // minus the tokenized and message from bank/tsp frontend
            const responseFromTSP: CardTokenizationResponse = {
                cardToken: event.data.cardToken,
                lastFour: event.data.lastFour,
                fullName: event.data.fullName,
                network: event.data.network,
                expMonth: event.data.expMonth,
                expYear: event.data.expYear,
            };

            // send the response as a request to payment processor backend
            console.log("Saving card ...");
            await AddCustomerCard(responseFromTSP);
            console.log("Card successfully saved!");

            // after a second of displaying message,
            // navigate user to their saved cards page
            setMessage(event.data.message);
            setHaveAddedCard(true);
        }

        // bank/tsp frontend postmessage() will be caught using this event listener
        window.addEventListener(
            "message",
            handleIframeMessage,
        );

        // unmount listener so next form uses new listener
        // removes unnecessary duplication, memory leaks, and unexpected behavior
        return () => {
            window.removeEventListener(
                "message",
                handleIframeMessage,
            );
        };
    }, []);

    useEffect(() => {
        if (!haveAddedCard) {
            return;
        }
        
        // after a second of displaying message,
        // navigate user to their saved cards page
        const timeout = setTimeout(() => {
            navigate("/view-saved-cards");
        }, 1000);

        return () => {
            clearTimeout(timeout);
        };
    }, [haveAddedCard, navigate]);

    return (
        <>
            <iframe
                src="http://localhost:4200"
                width="500"
                height="700"
                style={{
                    border: "none",
                }}
            />
            <div hidden={message === "" ? true : false}>
                <br />
                <p>{message}</p>
            </div>
        </>
    );
}