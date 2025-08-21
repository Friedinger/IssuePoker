<template>
  <v-row align="center">
    <v-row align="center">
      <v-col cols="auto"><h1>Issues</h1></v-col>
      <v-col
        v-if="getSearchQuery"
        cols="auto"
      >
        Gefiltert nach: {{ getSearchQuery }}
      </v-col>
    </v-row>
    <v-col
      v-if="getUser?.authorities.includes(ROLE_ADMIN)"
      cols="auto"
    >
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
      :search="getSearchQuery"
      :sort-by="sortedBy"
      @update:options="fetchIssues"
      @click:row="goToIssue"
    ></v-data-table-server>
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
import {
  ROLE_ADMIN,
  ROUTES_ISSUE_CREATE,
  ROUTES_ISSUE_DETAIL,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSearchQueryStore } from "@/stores/searchQuery.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";

const snackbarStore = useSnackbarStore();
const headers = [
  { key: "repo", title: "Repository" },
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

const { getUser } = storeToRefs(useUserStore());
const issues = ref<IssueSummary[]>([]);
const loading = ref(true);
const totalIssues = ref(0);
const sortedBy = ref<SortItem[]>([{ key: "number", order: "asc" }]);
const { getSearchQuery } = storeToRefs(useSearchQueryStore());

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
  sortedBy.value = sortBy;
  getIssueList(page - 1, itemsPerPage, sortBy, getSearchQuery.value ?? "")
    .then((content: Page<IssueSummary>) => {
      issues.value = content.content.map((issue) => ({
        ...issue,
        repo: `${issue.owner}/${issue.repository}`,
      }));
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
