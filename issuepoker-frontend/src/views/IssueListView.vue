<template>
  <v-container>
    <v-row><h1>Issue Liste</h1></v-row>
    <v-row>
      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :hover="true"
        :items="issues"
        :items-length="totalIssues"
        :loading="loading"
        loading-text="Laden... Bitte warten"
        @update:options="fetchIssues"
        @click:row="goToIssue"
      ></v-data-table-server>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type Issue from "@/types/Issue.ts";
import type Page from "@/types/Page.ts";

import { ref } from "vue";

import { getIssueList } from "@/api/fetch-issuelist.ts";
import { ROUTES_ISSUES_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const issues = ref<Issue[]>([]);
const loading = ref(true);
const itemsPerPage = ref(5);
const totalIssues = ref(0);
const headers = ref([
  { key: "id", title: "Nummer" },
  { key: "title", title: "Titel" },
  { key: "description", title: "Beschreibung" },
  { key: "votes", title: "Gepokert" },
]);

function fetchIssues({
  page,
  itemsPerPage,
}: {
  page: number;
  itemsPerPage: number;
}) {
  loading.value = true;
  getIssueList(page - 1, itemsPerPage)
    .then((content: Page<Issue>) => {
      issues.value = content.content;
      loading.value = false;
      totalIssues.value = content.page.totalElements;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function goToIssue(_: MouseEvent, props: { item: Issue; index: number }) {
  router.push({ name: ROUTES_ISSUES_DETAIL, params: { id: props.item.id } });
}
</script>
