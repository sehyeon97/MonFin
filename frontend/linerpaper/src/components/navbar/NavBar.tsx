import type { UserType } from "../../types/UserType";
import { NavBarItem } from "./NavBarItem";
import { navLinks } from "./NavLinks";

import '../../stylesheets/tab/Tab.css';

type NavBarProps = {
    userType: UserType,
};

export function NavBar({ userType }: NavBarProps) {
    const links = navLinks(userType);

    return (
        <nav className="nav-bar">
            <div className="nav-tabs">
                {links.map((item, index) => (
                    <NavBarItem key={index} item={item}/>
                ))}
            </div>
        </nav>
    );
}