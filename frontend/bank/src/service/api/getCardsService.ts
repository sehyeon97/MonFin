export async function getAllCards(): Promise<Response> {
  const endpoint: string =
    `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}` +
    `${process.env.NEXT_PUBLIC_BANK_CONTROLLER}` +
    `${process.env.NEXT_PUBLIC_BANK_OWNED_CARDS}`;

  return await fetch(endpoint, {
    method: "GET",
    credentials: "include",
  });
}
