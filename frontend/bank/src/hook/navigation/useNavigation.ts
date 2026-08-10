"use-client";

import { useRouter } from "next/navigation";

export function useNavigation() {
  const router = useRouter();

  return {
    goToAuthPage: () => router.push("/auth"),
    replaceWithHomePage: () => router.replace("/"),
  };
}
