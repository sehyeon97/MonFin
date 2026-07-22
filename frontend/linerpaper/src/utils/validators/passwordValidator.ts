export function passwordValidator(password: string): boolean {
  return /^[a-zA-Z0-9]{6,16}$/.test(password);
}
