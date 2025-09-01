import type { Filter } from "@/types/Filter.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export function defaultFilter(): Filter {
  return {
    search: null,
    owners: [],
    repositories: [],
    voted: null,
    resulted: null,
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
