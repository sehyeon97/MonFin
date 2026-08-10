import { useState } from "react";

export function useLoginForm() {
    const [loginForm, setLoginForm] = useState({
        username: "",
        password: "",
    });

    const setUsername = (username: string) =>
        setLoginForm(prevUsername => ({ ...prevUsername, username: username }));

    const setPassword = (password: string) =>
        setLoginForm(prevPassword => ({ ...prevPassword, password }));

    return { loginForm, setUsername, setPassword };
}