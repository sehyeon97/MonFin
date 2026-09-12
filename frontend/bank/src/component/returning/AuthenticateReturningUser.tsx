'use client'

import { useNavigation } from "@/hook/navigation/useNavigation";
import { MuffinIcon } from "@/loading/icon/Muffin";
import { Spinner } from "@/loading/Spinner";
import { validateReturningUser } from "@/service/api/auth/authService";
import { useEffect, useState } from "react";

// As a user that has closed our web app by exiting the tab, but remaining on the same browser,
// when coming back to the web app on a new tab,
// validate that the access token is still valid then show home page when valid or auth form when invalid.
export function AuthenticateReturningUser({ children }: { children: React.ReactNode }) {
    const [checking, setChecking] = useState(true);
    const navigator = useNavigation();

    useEffect(() => {
        async function checkAuthentication() {
            try {
                const response = await validateReturningUser();
                if (!response.ok) {
                    navigator.goToAuthPage();
                    return;
                }

                setChecking(false);
            } catch {
                navigator.replaceWithAuthPage();
            }
        }

        checkAuthentication();
    }, [navigator]);

    if (checking) {
        return <Spinner icon={<MuffinIcon />} />
    }

    return children;
}