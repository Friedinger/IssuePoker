import { ROLE_ADMIN, ROLE_USER } from "@/constants.ts";
import { useUserStore } from "@/stores/user.ts";

export function isLoggedIn(): boolean {
  return useUserStore().getUser !== null;
}

export function isUser(): boolean {
  return hasGroup(ROLE_USER);
}

export function isAdmin(): boolean {
  return hasGroup(ROLE_ADMIN);
}

export function hasGroup(groups: string | string[]): boolean {
  const userAuthorities = useUserStore().getUser?.authorities ?? [];
  if (Array.isArray(groups)) {
    return groups.some((group) => userAuthorities.includes(group));
  }
  return userAuthorities.includes(groups);
}
