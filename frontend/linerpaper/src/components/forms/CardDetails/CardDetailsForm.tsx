import type { CardTokenizationRequest } from "../../../dto/tsp/CardTokenizationRequest";
import { useCardDetailsForm } from "../../../hooks/card/useCardDetailsForm";
import { CvvInput } from "./CvvInput";
import { ExpiryMonthInput } from "./ExpiryMonthInput";
import { ExpiryYearInput } from "./ExpiryYearInput";
import { FullNameInput } from "./FullNameInput";
import { PanInput } from "./PanInput";

type CardDetailsProps = {
    onSubmit: (req: CardTokenizationRequest) => Promise<void>;
};

export function CardDetailsForm({ onSubmit }: CardDetailsProps) {
    const { cardDetails, setPan, setCVV, setFullName, setExpMonth, setExpYear } = useCardDetailsForm();

    function handleSubmit(event: React.SubmitEvent): void {
        event.preventDefault();

        const request: CardTokenizationRequest = {
            pan: cardDetails.pan.trim().replace(/\s/g, ""), // remove all whitespaces
            cvv: cardDetails.cvv,
            fullName: cardDetails.fullName,
            expMonth: cardDetails.expMonth,
            expYear: cardDetails.expYear,
        };

        onSubmit(request);
    }

    return (
        <form onSubmit={handleSubmit}>
            <PanInput value={cardDetails.pan} onChange={setPan} />
            <CvvInput value={cardDetails.cvv} onChange={setCVV} />
            <FullNameInput value={cardDetails.fullName} onChange={setFullName} />
            <ExpiryMonthInput value={cardDetails.expMonth} onChange={setExpMonth} />
            <ExpiryYearInput value={cardDetails.expYear} onChange={setExpYear} />
            <button>Add Card</button>
        </form>
    );
}