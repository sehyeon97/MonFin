import { NavLink } from "react-router-dom";
import type { NavLink as NavLinkItem } from "./NavLinks";

interface NavItemProps {
    item: NavLinkItem;
}

export function NavBarItem({ item }: NavItemProps) {
    return (
        // NavLink knows when the page is currently active or not
        // So only when the provided link is not the current page,
        // it activates
        <NavLink to={item.path}>{item.label}</NavLink>
    );
}