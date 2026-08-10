"use client";

import { Container } from "@/component/Container";
import { AuthForm } from "@/component/form/AuthForm";
import { LoginUserCredentials } from "@/dtos/auth/auth.login-request";
import { CreateBankAccountRequest } from "@/dtos/auth/auth.signup-request";
import { useNavigation } from "@/hook/navigation/useNavigation";
import { loginUser, signupUser } from "@/service/handler/authHandler";
import { LOGIN_CONTAINER_CUSTOM_CLASS_NAME } from "@/style/classnames";
import { useState } from "react";

export default function Auth() {
    const [error, setError] = useState("");

    const navigator = useNavigation();

    async function onLogin(request: LoginUserCredentials) {
        const data: string = await loginUser(request);

        if (data) {
            navigator.replaceWithHomePage();
            return;
        }

        setError(data);
    }

    async function onSignup(request: CreateBankAccountRequest) {
        const data: string = await signupUser(request);

        if (data) {
            navigator.replaceWithHomePage();
            return;
        }

        setError(data);
    }

    return (
        <main className="min-h-screen">
            <div className="mx-auto mt-20 w-[16.6667vw]">
                <Container title="Login" className={LOGIN_CONTAINER_CUSTOM_CLASS_NAME}>
                    <AuthForm onLogin={onLogin} onSignup={onSignup} error={error}></AuthForm>
                </Container>
            </div>
        </main>
    );
}