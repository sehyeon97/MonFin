import { UserTypes, type UserType } from "../../types/UserType";

export interface NavLink {
  label: string;
  path: string;
}

// if we want to add or remove an item, we simply add it here
export const navLinks = (userType: UserType): NavLink[] => {
  const links: NavLink[] = [];

  if (userType === UserTypes.Customer) {
    links.push({
      label: "Shopping",
      path: "/shopping",
    });
  }

  if (userType === UserTypes.Merchant) {
    links.push({
      label: "My Business",
      path: "/my-business",
    });
  }

  links.push({
    label: "Save Card",
    path: "/save-card-information",
  });

  links.push({
    label: "View my cards",
    path: "/view-saved-cards",
  });

  return links;
};
