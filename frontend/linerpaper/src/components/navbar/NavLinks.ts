export interface NavLink {
  label: string;
  path: string;
}

// if we want to add or remove an item, we simply add it here
export const navLinks: NavLink[] = [
  {
    label: "Shopping",
    path: "/shopping",
  },
  {
    label: "Save Card",
    path: "/save-card-information",
  },
  {
    label: "View my cards",
    path: "/view-saved-cards",
  },
];
