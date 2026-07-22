export function emailValidator(email: string): boolean {
  return email.trim() !== "" && email.includes("@gmail.com");
}
