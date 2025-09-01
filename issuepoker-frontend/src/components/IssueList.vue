<template>
  <v-row align="center">
    <v-col><h1>Issues</h1></v-col>
    <v-col
      v-if="getUser?.authorities.includes(ROLE_ADMIN)"
      cols="auto"
    >
      <v-btn :to="{ name: ROUTES_ISSUE_CREATE }">Neues Issue</v-btn>
    </v-col>
  </v-row>
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
  <v-row>
    <v-data-table-server
      :headers="headers"
      :hover="true"
      :items="issues"
      :items-length="totalIssues"
      :items-per-page="itemsPerPage"
      :items-per-page-options="itemsPerPageOptions"
      :loading="loading"
      :page="page"
      :search="searchQuery"
      :sort-by="sortBy"
      multi-sort
      @update:options="updateOptions"
      @click:row="goToIssue"
    />
  </v-row>
</template>

<script lang="ts" setup>
import type { FilterOptions } from "@/types/FilterOptions.ts";
import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort.js";

import {
  mdiHomeAccount,
  mdiMagnify,
  mdiSourceRepository,
  mdiTrophy,
  mdiVote,
} from "@mdi/js";
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import { getFilterOptions } from "@/api/fetch-filterOptions.ts";
import { getIssueList } from "@/api/fetch-issueList.ts";
import {
  ROLE_ADMIN,
  ROUTES_HOME,
  ROUTES_ISSUE_CREATE,
  ROUTES_ISSUE_DETAIL,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useFilterStore } from "@/stores/filter.ts";
import { useSearchQueryStore } from "@/stores/searchQuery.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";

const snackbarStore = useSnackbarStore();
const headers = [
  { key: "owner", title: "Besitzer" },
  { key: "repository", title: "Repository" },
  { key: "number", title: "Nummer" },
  { key: "title", title: "Titel" },
  { key: "voteCount", title: "Anzahl Stimmen", sortable: false },
  { key: "voteResult", title: "Ergebnis", sortable: false },
];
const itemsPerPageOptions = [
  { value: 5, title: "5" },
  { value: 10, title: "10" },
  { value: 25, title: "25" },
  { value: 50, title: "50" },
  { value: 100, title: "100" },
  { value: -1, title: "$vuetify.dataFooter.itemsPerPageAll" },
];
const selectOptions = [
  { value: null, title: "Egal" },
  { value: true, title: "Ja" },
  { value: false, title: "Nein" },
];

const { getUser } = storeToRefs(useUserStore());
const issues = ref<IssueSummary[]>([]);
const loading = ref(true);
const totalIssues = ref(0);
const page = ref<number>(1);
const itemsPerPage = ref<number>(10);
const sortBy = ref<SortItem[]>([
  { key: "owner", order: "asc" },
  { key: "repository", order: "asc" },
  { key: "number", order: "asc" },
]);
const searchQueryStore = useSearchQueryStore();
const searchQuery = computed({
  get: () => searchQueryStore.getSearchQuery,
  set: (value) => {
    searchQueryStore.setSearchQuery(value);
  },
});
const filterOptions = ref<FilterOptions>({
  owners: [],
  repositories: [],
});
const filterStore = useFilterStore();
const filter = computed({
  get: () => filterStore.getFilter,
  set: (value) => {
    filterStore.setFilter(value);
    fetchIssues();
  },
});

onMounted(() => {
  getFilterOptions()
    .then((content: FilterOptions) => (filterOptions.value = content))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
});

function fetchIssues() {
  loading.value = true;
  getIssueList(
    page.value - 1,
    itemsPerPage.value,
    sortBy.value,
    searchQuery.value ?? "",
    filter.value
  )
    .then((content: Page<IssueSummary>) => {
      issues.value = content.content;
      loading.value = false;
      totalIssues.value = content.page.totalElements;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function updateOptions({
  page: newPage,
  itemsPerPage: newItemsPerPage,
  sortBy: newSortBy,
}: {
  page: number;
  itemsPerPage: number;
  sortBy: SortItem[];
}) {
  page.value = newPage;
  itemsPerPage.value = newItemsPerPage;
  sortBy.value = newSortBy;
  fetchIssues();
}

function goToIssue(
  _: MouseEvent,
  props: { item: IssueDetails; index: number }
) {
  router.push({
    name: ROUTES_ISSUE_DETAIL,
    params: {
      owner: props.item.owner,
      repository: props.item.repository,
      number: props.item.number,
    },
  });
}

function search() {
  searchQueryStore.setSearchQuery(searchQuery.value);
  if (searchQuery.value && searchQuery.value != "") {
    router.push({ name: ROUTES_HOME, query: { search: searchQuery.value } });
  } else {
    router.push({ name: ROUTES_HOME });
  }
}
</script>
