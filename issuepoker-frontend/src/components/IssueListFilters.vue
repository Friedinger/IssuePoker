<template>
  <v-col>
    <v-row>
      <v-col
        cols="12"
        sm="4"
      >
        <v-text-field
          v-model="filter.search"
          :prepend-inner-icon="mdiMagnify"
          clearable
          density="compact"
          hide-details
          label="Suche (Titel, Beschreibung)"
          variant="outlined"
          @update:model-value="setQuery"
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
          :clearable="isDefined(filter.voted)"
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
          :clearable="isDefined(filter.resulted)"
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
import type { Filter } from "@/types/Filter.ts";
import type { LocationQuery } from "vue-router";

import {
  mdiHomeAccount,
  mdiMagnify,
  mdiRestore,
  mdiSourceRepository,
  mdiTrophy,
  mdiVote,
} from "@mdi/js";
import { isDefined } from "@vueuse/core";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getFilterOptions } from "@/api/issue/get-filterOptions.ts";
import router from "@/plugins/router.ts";
import { defaultFilter, useFilterStore } from "@/stores/filter.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { filterFromQuery, filterToQuery } from "@/types/Filter.ts";

const selectOptions = [
  { value: null, title: "Egal" },
  { value: true, title: "Ja" },
  { value: false, title: "Nein" },
];

const emit = defineEmits<(e: "fetchIssues") => void>();
const snackbarStore = useSnackbarStore();
const filter = computed({
  get: () => useFilterStore().getFilter,
  set: (value) => {
    useFilterStore().setFilter(value);
    fetchIssues();
  },
});
const filterOptions = ref<Filter>(defaultFilter());
const route = useRoute();

onMounted(() => {
  getFilterOptions()
    .then((content: Filter) => (filterOptions.value = content))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
  parseQuery(route.query);
  setQuery();
});

watch(
  () => route.fullPath,
  () => parseQuery(route.query)
);

function fetchIssues() {
  setQuery();
  emit("fetchIssues");
}

function resetFilters() {
  filter.value = defaultFilter();
  fetchIssues();
}

function setQuery() {
  router.replace({ query: filterToQuery(filter.value) });
}

function parseQuery(query: LocationQuery) {
  if (Object.keys(query).length === 0) return;
  filter.value = filterFromQuery(query);
}
</script>
