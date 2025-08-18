import { defineStore } from "pinia";
import { computed, ref } from "vue";

export type SearchQuery = string;

export const useSearchQueryStore = defineStore("searchQuery", () => {
  const searchQuery = ref<SearchQuery>("");

  const getSearchQuery = computed((): SearchQuery => {
    return searchQuery.value;
  });

  function setSearchQuery(payload: SearchQuery): void {
    searchQuery.value = payload;
  }

  return { getSearchQuery, setSearchQuery };
});
