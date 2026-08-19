'use client';

import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";
import { useLoginForm } from "@/hook/auth/useLoginForm";
import { useSignupForm } from "@/hook/auth/useSignupForm";
import { MuffinIcon } from "@/loading/icon/Muffin";
import { Spinner } from "@/loading/Spinner";
import { LOGIN_FORM_CLASS_NAME, LOGIN_FORM_INPUT_CLASS_NAME } from "@/style/classnames";
import { useState } from "react";

interface AuthFormProps {
    onLogin: (request: LoginUserCredentials) => void;
    onSignup: (request: CreateBankAccountRequest) => void;
    error?: string;
}

export function AuthForm({ onLogin, onSignup, error }: AuthFormProps) {
    const { loginForm, setUsername, setPassword } = useLoginForm();
    const { signupForm, setFullName, setPhoneNumber } = useSignupForm();

    // true = login form
    // false = signup form
    const [isLogin, setIsLogin] = useState(true);

    // when form is submitted
    const [isLoading, setIsLoading] = useState(false);

    function onFormSubmit(event: React.SubmitEvent) {
        event.preventDefault();
        setIsLoading(true);

        let request: LoginUserCredentials | CreateBankAccountRequest;

        if (isLogin) {
            request = {
                username: loginForm.username,
                password: loginForm.password,
            };
            onLogin(request);
        } else {
            request = {
                username: loginForm.username,
                password: loginForm.password,
                fullName: signupForm.fullName,
                phoneNumber: signupForm.phoneNumber,
            };
            onSignup(request);
        }
        setIsLoading(false);
    }

    function onCreateAccountClick() {
        setIsLogin(false);
    }

    function onBackToLoginClick() {
        setIsLogin(true);
    }

    return (
        <form className={LOGIN_FORM_CLASS_NAME} onSubmit={onFormSubmit}>
            <input
                className={`${LOGIN_FORM_INPUT_CLASS_NAME}`}
                spellCheck={false}
                title="Username" type="text"
                placeholder="Enter Username"
                value={loginForm.username}
                onChange={(event) => setUsername(event.target.value)}
            />
            <input
                className={`${LOGIN_FORM_INPUT_CLASS_NAME}`}
                title="Password" type="password"
                minLength={6} maxLength={16}
                placeholder="Enter Password"
                value={loginForm.password}
                onChange={(event) => setPassword(event.target.value)}
            />

            {!isLogin && <>
            <input
                className={`${LOGIN_FORM_INPUT_CLASS_NAME}`}
                title="Full name" type="text"
                placeholder="Your Full Name"
                value={signupForm.fullName}
                onChange={(event) => setFullName(event.target.value)}
            />
            <input
                className={`${LOGIN_FORM_INPUT_CLASS_NAME}`}
                title="Phone Number" type="text"
                placeholder="Enter Phone Number"
                value={signupForm.phoneNumber}
                onChange={(event) => setPhoneNumber(event.target.value)}
            />
            </>}
            <button disabled={isLoading}>
                {isLoading ? <Spinner icon={<MuffinIcon />} /> : isLogin ? "Login" : "Signup"}
            </button>
            {isLogin ? 
                <footer onClick={onCreateAccountClick}>Create an account</footer> : 
                <footer onClick={onBackToLoginClick}>Back to login</footer>}
            {error && <p>{error}</p>}
        </form>
    );
}