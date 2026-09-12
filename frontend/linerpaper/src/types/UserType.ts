export const UserTypes = {
  Customer: "customer",
  Merchant: "merchant",
} as const;

export type UserType = (typeof UserTypes)[keyof typeof UserTypes];
