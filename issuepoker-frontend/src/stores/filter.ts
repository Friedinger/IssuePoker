import type { FilterOptions } from "@/types/FilterOptions.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useFilterStore = defineStore("filterStore", () => {
  const filter = ref<FilterOptions>({
    owners: [],
    repositories: [],
  });

  const getFilter = computed((): FilterOptions => {
    return filter.value;
  });

  function setFilter(payload: FilterOptions): void {
    filter.value = payload;
  }

  return { getFilter, setFilter };
});
