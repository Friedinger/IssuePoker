<template>
  <v-row align="center">
    <v-col>
      <h1>Issues</h1>
    </v-col>
    <v-col cols="auto">
      <v-btn :to="{ name: ROUTES_ISSUE_CREATE }">Neues Issue</v-btn>
    </v-col>
  </v-row>
  <v-row>
    <v-data-table-server
      :headers="headers"
      :hover="true"
      :items="issues"
      :items-length="totalIssues"
      :items-per-page-options="itemsPerPageOptions"
      :loading="loading"
      items-per-page-text="Issues pro Seite:"
      loading-text="Issues werden geladen... Bitte warten."
      @update:options="fetchIssues"
      @click:row="goToIssue"
    ></v-data-table-server>
  </v-row>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort";

import { ref } from "vue";

import { getIssueList } from "@/api/fetch-issuelist.ts";
import { ROUTES_ISSUE_CREATE, ROUTES_ISSUE_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const headers = [
  { key: "id", title: "Nummer" },
  { key: "title", title: "Titel" },
  { key: "voteCount", title: "Anzahl Stimmen" },
];
const itemsPerPageOptions = [
  { value: 5, title: "5" },
  { value: 10, title: "10" },
  { value: 25, title: "25" },
  { value: 50, title: "50" },
  { value: 100, title: "100" },
  { value: -1, title: "Alle" },
];

const issues = ref<IssueSummary[]>([]);
const loading = ref(true);
const totalIssues = ref(0);

function fetchIssues({
  page,
  itemsPerPage,
  sortBy,
}: {
  page: number;
  itemsPerPage: number;
  sortBy: SortItem[];
}) {
  loading.value = true;
  getIssueList(page - 1, itemsPerPage, sortBy)
    .then((content: Page<IssueSummary>) => {
      issues.value = content.content;
      loading.value = false;
      totalIssues.value = content.page.totalElements;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function goToIssue(
  _: MouseEvent,
  props: { item: IssueDetails; index: number }
) {
  router.push({ name: ROUTES_ISSUE_DETAIL, params: { id: props.item.id } });
}
</script>
