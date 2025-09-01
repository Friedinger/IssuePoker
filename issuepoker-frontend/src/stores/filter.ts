import type { Filter } from "@/types/Filter.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export function defaultFilter(): Filter {
  return {
    search: undefined,
    owners: [],
    repositories: [],
    voted: undefined,
    resulted: undefined,
  };
}

export const useFilterStore = defineStore("filterStore", () => {
  const filter = ref<Filter>(defaultFilter());

  const getFilter = computed((): Filter => {
    return filter.value;
  });

  function setFilter(payload: Filter): void {
    filter.value = payload;
  }

  return { getFilter, setFilter };
});
