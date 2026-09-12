import { Outlet } from "react-router-dom";
import { NavBar } from "../components/navbar/NavBar";
import type { UserType } from "../types/UserType";

type HomeLayoutProps = {
    userType: UserType,
};

export function HomeLayout({ userType }: HomeLayoutProps) {
    return (
        <>
            <NavBar userType={userType} />
            <main>
                {/* render nested routes */}
                <Outlet />
            </main>
        </>
    );
}