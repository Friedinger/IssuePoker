<template>
  <v-col>
    <v-row>
      <v-col
        cols="12"
        sm="4"
      >
        <v-text-field
          v-model="searchQuery"
          :prepend-inner-icon="mdiMagnify"
          clearable
          density="compact"
          hide-details
          label="Suche (Titel, Beschreibung)"
          variant="outlined"
          @keyup.enter="search"
          @click:clear="search"
        />
      </v-col>
      <v-col
        cols="12"
        lg="2"
        sm="4"
      >
        <v-autocomplete
          v-model="filter.owners"
          :items="filterOptions.owners"
          :prepend-inner-icon="mdiHomeAccount"
          chips
          clearable
          density="compact"
          hide-details
          label="Besitzer"
          multiple
          variant="outlined"
          @update:model-value="fetchIssues"
        />
      </v-col>
      <v-col
        cols="12"
        lg="2"
        sm="4"
      >
        <v-autocomplete
          v-model="filter.repositories"
          :items="filterOptions.repositories"
          :prepend-inner-icon="mdiSourceRepository"
          chips
          clearable
          density="compact"
          hide-details
          label="Repository"
          multiple
          variant="outlined"
          @update:model-value="fetchIssues"
        />
      </v-col>
      <v-col
        cols="12"
        lg="2"
        sm="4"
      >
        <v-select
          v-model="filter.voted"
          :items="selectOptions"
          :prepend-inner-icon="mdiVote"
          density="compact"
          hide-details
          label="Abgestimmt"
          variant="outlined"
          @update:model-value="fetchIssues"
        />
      </v-col>
      <v-col
        cols="12"
        lg="2"
        sm="4"
      >
        <v-select
          v-model="filter.resulted"
          :items="selectOptions"
          :prepend-inner-icon="mdiTrophy"
          density="compact"
          hide-details
          label="Ergebnis"
          variant="outlined"
          @update:model-value="fetchIssues"
        />
      </v-col>
    </v-row>
  </v-col>
  <v-col cols="auto">
    <v-tooltip
      location="top"
      text="Filter zurücksetzen"
    >
      <template v-slot:activator="{ props }">
        <v-btn
          :icon="mdiRestore"
          density="comfortable"
          rounded="rounded"
          v-bind="props"
          @click="resetFilters"
        />
      </template>
    </v-tooltip>
  </v-col>
</template>
<script lang="ts" setup>
import type { FilterOptions } from "@/types/FilterOptions.ts";

import {
  mdiHomeAccount,
  mdiMagnify,
  mdiRestore,
  mdiSourceRepository,
  mdiTrophy,
  mdiVote,
} from "@mdi/js";
import { computed, onMounted, ref } from "vue";

import { getFilterOptions } from "@/api/fetch-filterOptions.ts";
import { defaultFilter, useFilterStore } from "@/stores/filter.ts";
import { useSearchQueryStore } from "@/stores/searchQuery.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const selectOptions = [
  { value: null, title: "Egal" },
  { value: true, title: "Ja" },
  { value: false, title: "Nein" },
];

const emit = defineEmits<(e: "fetchIssues") => void>();
const snackbarStore = useSnackbarStore();
const searchQuery = computed({
  get: () => useSearchQueryStore().getSearchQuery,
  set: (value) => {
    useSearchQueryStore().setSearchQuery(value);
  },
});
const filter = computed({
  get: () => useFilterStore().getFilter,
  set: (value) => {
    useFilterStore().setFilter(value);
    fetchIssues();
  },
});
const filterOptions = ref<FilterOptions>(defaultFilter());

onMounted(() => {
  getFilterOptions()
    .then((content: FilterOptions) => (filterOptions.value = content))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
});

function search() {
  // if (searchQuery.value && searchQuery.value != "") {
  //   router.push({ name: ROUTES_HOME, query: { search: searchQuery.value } });
  // } else {
  //   router.push({ name: ROUTES_HOME });
  // }
}

function resetFilters() {
  filter.value = defaultFilter();
  searchQuery.value = "";
  fetchIssues();
}

function fetchIssues() {
  emit("fetchIssues");
}
</script>
