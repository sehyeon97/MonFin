/*
 * Converts the backend's Java Instant to a user-friendly string
 * (also removes ReactNode exception on {.map()})
 * Param example: 2026-08-25T07:18:32.123Z
 * Return example: 08/25/2026 12:18 AM
 */
export function formatInstant(instant: string): string {
  return new Date(instant)
    .toLocaleString("en-US", {
      month: "2-digit",
      day: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    })
    .replace(",", "");
}
