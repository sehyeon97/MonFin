import { useState } from "react";

export function useLoginForm() {
    const [loginForm, setLoginForm] = useState({
        email: "",
        password: "",
    });

    // React automatically passes in previous state
    // Because states are immutable, ...something copies the state
    // Then rewrites it to the param value
    const setEmail = (email: string) =>
        setLoginForm(prevEmail => ({ ...prevEmail, email }));

    const setPassword = (password: string) =>
        setLoginForm(prevPassword => ({ ...prevPassword, password }));

    return { loginForm, setEmail, setPassword };
}