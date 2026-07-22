import { NavBarItem } from "./NavBarItem";
import { navLinks } from "./NavLinks";

export function NavBar() {
    return (
        <nav>
            <div>
                {navLinks.map(item => (
                    <NavBarItem item={item}/>
                ))}
            </div>
        </nav>
    );
}