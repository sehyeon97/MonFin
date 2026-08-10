import { useState } from "react";

export function useSignupForm() {
    const [signupForm, setSignupForm] = useState({
        fullName: "",
        phoneNumber: "",
    });

    const setFullName = (fullName: string) =>
        setSignupForm(prevFullName => ({ ...prevFullName, fullName: fullName }));

    const setPhoneNumber = (phoneNumber: string) =>
        setSignupForm(prevPhoneNumber => ({ ...prevPhoneNumber, phoneNumber: phoneNumber }));

    return { signupForm, setFullName, setPhoneNumber };
}