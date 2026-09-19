import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import '../../stylesheets/popups/SessionTimeout.css';

const SESSION_DURATION = 15 * 60 * 1000;
const WARNING_TIME = 14 * 60 * 1000;

export default function SessionTimeout() {
    const navigate = useNavigate();
    const [showWarning, setShowWarning] = useState(false);
    const [secondsLeft, setSecondsLeft] = useState(60);

    useEffect(() => {
        const warningTimer = setTimeout(() => {
            setShowWarning(true);
        }, WARNING_TIME);

        const redirectTimer = setTimeout(() => {
            navigate("/");
        }, SESSION_DURATION);

        return () => {
            clearTimeout(warningTimer);
            clearTimeout(redirectTimer);
        };
    }, [navigate]);

    useEffect(() => {
        if (!showWarning) return;

        const countdown = setInterval(() => {
            setSecondsLeft((prev) => {
                if (prev <= 1) {
                    clearInterval(countdown);
                    return 0;
                }

                return prev - 1;
            });
        }, 1000);

        return () => clearInterval(countdown);
    }, [showWarning]);

    if (!showWarning) {
        return null;
    }

    return (
        <div className="session-warning">
            <p>Session expiring soon.</p>
            <p>Will be redirected to login page in {secondsLeft}s.</p>

            <button onClick={() => navigate("/")}>
                Return to login now
            </button>
        </div>
    );
}
