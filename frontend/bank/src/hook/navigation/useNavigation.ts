"use-client";

import { useRouter } from "next/navigation";
import { useCallback, useMemo } from "react";

/** self-note for learning
 * React creates new objects and functions every time a component renders
 * useCallback and useMemo keeps same references between renders and prevents re-creation
 * useCallback: gives same callback between renders unless dependency(ies) changes
 * It becomes useful if useNavigation is part of useEffect.
 * For example, this prevents the home page going back to auth form on re-render
 * because it still uses the same router, meaning useEffect won't run again
 * useMemo: Reuse the provided object(s) until one of the functions changes
 *
 * Simple version: useCallback = stabilize function | useMemo = stabilize object containing function
 * Now, useEffect will realize that navigator is the same object and won't run useEffect more than once.
 * | This concept was implemented after home page redirected to auth form automatically every time. |
 */
export function useNavigation() {
  const router = useRouter();

  const goToAuthPage = useCallback(() => {
    router.push("/auth");
  }, [router]);

  const replaceWithAuthPage = useCallback(() => {
    router.replace("/auth");
  }, [router]);

  const replaceWithHomePage = useCallback(() => {
    router.replace("/");
  }, [router]);

  return useMemo(
    () => ({
      goToAuthPage,
      replaceWithAuthPage,
      replaceWithHomePage,
    }),
    [goToAuthPage, replaceWithAuthPage, replaceWithHomePage],
  );
}
