import { useState } from "react";

export function useCardDetailsForm() {
    const [cardDetails, setCardDetails] = useState({
        pan: "",
        cvv: "",
        fullName: "",
        expMonth: "",
        expYear: "",
    });

    const setPan = (pan: string) => 
        setCardDetails((prevPan) => ({...prevPan, pan}));

    const setCVV = (cvv: string) =>
        setCardDetails((prevCVV) => ({...prevCVV, cvv}));

    const setFullName = (fullName: string) =>
        setCardDetails((prevFullName) => ({...prevFullName, fullName}));

    const setExpMonth = (expMonth: string) =>
        setCardDetails((prevExpMonth) => ({...prevExpMonth, expMonth}));

    const setExpYear = (expYear: string) =>
        setCardDetails((prevExpYear) => ({...prevExpYear, expYear}));

    return { cardDetails, setPan, setCVV, setFullName, setExpMonth, setExpYear };
}