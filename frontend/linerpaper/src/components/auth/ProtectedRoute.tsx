import { Navigate } from "react-router-dom";
import type { UserType } from "../../types/UserType";

interface ProtectedRouteProps {
    userType: UserType;
    allowedUserType: UserType;
    children: React.ReactNode;
}

export default function ProtectedRoute({
    userType,
    allowedUserType,
    children
}: ProtectedRouteProps) {
    if (userType !== allowedUserType) {
        return <Navigate to="/" replace />;
    }

    return children;
}