import { useState } from "react";

export function useSignupForm() {
    const [signupForm, setSignupForm] = useState({
        address: "",
        city: "",
        state: "",
        zip: "",
    });

    const setAddress = (address: string) =>
        setSignupForm(prevAddress => ({ ...prevAddress, address }));

    const setCity = (city: string) =>
        setSignupForm(prevCity => ({ ...prevCity, city }));

    const setState = (state: string) =>
        setSignupForm(prevState => ({ ...prevState, state }));

    const setZip = (zip: string) =>
        setSignupForm(prevZip => ({ ...prevZip, zip }));

    return { signupForm, setAddress, setCity, setState, setZip };
}