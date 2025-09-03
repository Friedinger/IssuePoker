import type User from "@/types/User";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

type UserState = User | null;

export const useUserStore = defineStore("user", () => {
  const user = ref<UserState>(null);

  const getUser = computed((): UserState => {
    return user.value;
  });

  function setUser(payload: UserState): void {
    user.value = payload;
  }

  return { getUser, setUser };
});
