import { Outlet } from "react-router-dom";
import { NavBar } from "../components/navbar/NavBar";

export function HomeLayout() {
    return (
        <>
            <NavBar />
            <main>
                {/* render nested routes */}
                <Outlet />
            </main>
        </>
    );
}