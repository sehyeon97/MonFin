import { logoutUser, refreshUserSession } from "@/service/handler/authHandler";
import { useCallback, useEffect, useState } from "react";

// written in ms
// 14 minutes (Access token lasts 15min)
const TIME_UNTIL_POPUP = 14 * 60 * 1000;
// logout if no interaction with popup (30 seconds | doesn't wait the whole minute)
const POPUP_TIMER = 30 * 1000;

export function useSession() {
    const [showPopup, setShowPopup] = useState(false);

    // useEffect resets 14min timer when session is renewed
    const [sessionVersion, setSessionVersion] = useState(0);

    const stayLoggedIn = useCallback(async () => {
        try {
            await refreshUserSession();
            setSessionVersion(prev => prev + 1);
            setShowPopup(false);
        } catch (error) {
            console.error("Failed to extend session: ", error);
            await logoutUser();
            window.location.href = "/auth";
        }
    }, []);

    const logout = useCallback(async () => {
        try {
            await logoutUser();
            setShowPopup(false);
            // Redirect to auth form
            window.location.href = "/auth";
        } catch (error) {
            console.error("Logout failed:", error);
        }
    }, []);

    // reset timer on every "Stay logged in" click
    useEffect(() => {
        const refreshTimer = setTimeout(() => {
            setShowPopup(true);
        }, TIME_UNTIL_POPUP);

        const popupTimer = setTimeout(() => {
            logout();
        }, TIME_UNTIL_POPUP + POPUP_TIMER);

        return () => {
            clearTimeout(refreshTimer);
            clearTimeout(popupTimer);
        }
    }, [sessionVersion, logout]);

    return { showPopup, stayLoggedIn, logout }
}