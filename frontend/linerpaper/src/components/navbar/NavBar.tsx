import type { UserType } from "../../types/UserType";
import { NavBarItem } from "./NavBarItem";
import { navLinks } from "./NavLinks";

type NavBarProps = {
    userType: UserType,
};

export function NavBar({ userType }: NavBarProps) {
    const links = navLinks(userType);

    return (
        <nav>
            <div>
                {links.map((item, index) => (
                    <NavBarItem key={index} item={item}/>
                ))}
            </div>
        </nav>
    );
}