import type { FilterOptions } from "@/types/FilterOptions.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const defaultFilter: FilterOptions = {
  owners: [],
  repositories: [],
  voted: null,
  resulted: null,
};

export const useFilterStore = defineStore("filterStore", () => {
  const filter = ref<FilterOptions>(defaultFilter);

  const getFilter = computed((): FilterOptions => {
    return filter.value;
  });

  function setFilter(payload: FilterOptions): void {
    filter.value = payload;
  }

  function resetFilter(): void {
    filter.value = defaultFilter;
  }

  return { getFilter, setFilter, resetFilter };
});
