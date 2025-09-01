import type { FilterOptions } from "@/types/FilterOptions.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export function defaultFilter(): FilterOptions {
  return {
    owners: [],
    repositories: [],
    voted: null,
    resulted: null,
  };
}

export const useFilterStore = defineStore("filterStore", () => {
  const filter = ref<FilterOptions>(defaultFilter());

  const getFilter = computed((): FilterOptions => {
    return filter.value;
  });

  function setFilter(payload: FilterOptions): void {
    filter.value = payload;
  }

  return { getFilter, setFilter };
});
