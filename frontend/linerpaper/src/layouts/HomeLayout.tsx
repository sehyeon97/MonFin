import { Outlet } from "react-router-dom";

import { NavBar } from "../components/navbar/NavBar";
import type { UserType } from "../types/UserType";
import SessionTimeout from "../components/auth/SessionTimeout";

type HomeLayoutProps = {
    userType: UserType,
};

export function HomeLayout({ userType }: HomeLayoutProps) {
    return (
        <>
            <SessionTimeout />
            <NavBar userType={userType} />
            <main>
                {/* render nested routes */}
                <Outlet />
            </main>
        </>
    );
}