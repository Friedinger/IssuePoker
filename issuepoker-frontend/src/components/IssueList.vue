<template>
  <v-row align="center">
    <v-col><h1>Issues</h1></v-col>
    <v-col
      v-if="isAdmin()"
      cols="auto"
    >
      <v-btn :to="{ name: ROUTES_ISSUE_NEW }">Neues Issue</v-btn>
    </v-col>
  </v-row>
  <v-row>
    <issue-list-filters @fetchIssues="fetchIssues" />
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
      :search="getFilter.search"
      :sort-by="sortBy"
      multi-sort
      @update:options="updateOptions"
      @click:row="goToIssue"
    />
  </v-row>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort.js";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { getIssueList } from "@/api/fetch-issueList.ts";
import IssueListFilters from "@/components/IssueListFilters.vue";
import { ROUTES_ISSUE_DETAIL, ROUTES_ISSUE_NEW } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useFilterStore } from "@/stores/filter.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { isAdmin } from "@/util/userUtils.ts";

const headers = [
  { key: "owner", title: "Besitzer" },
  { key: "repository", title: "Repository" },
  { key: "number", title: "Nummer" },
  { key: "title", title: "Titel" },
  { key: "voteCount", title: "Stimmen", sortable: false },
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

const snackbarStore = useSnackbarStore();
const { getFilter } = storeToRefs(useFilterStore());

const loading = ref(true);
const issues = ref<IssueSummary[]>([]);
const totalIssues = ref(0);
const page = ref<number>(1);
const itemsPerPage = ref<number>(10);
const sortBy = ref<SortItem[]>([
  { key: "owner", order: "asc" },
  { key: "repository", order: "asc" },
  { key: "number", order: "asc" },
]);

function fetchIssues() {
  loading.value = true;
  getIssueList(
    page.value - 1,
    itemsPerPage.value,
    sortBy.value,
    getFilter.value
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
</script>
