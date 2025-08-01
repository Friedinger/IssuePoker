<template>
  <v-container>
    <v-row><h1>Issue Liste</h1></v-row>
    <v-row>
      <v-col>
        <v-list lines="two">
          <v-list-item
            v-for="issue in issues"
            :key="issue.id"
            @click="goToIssue(issue.id)"
          >
            <v-list-item-title>{{ issue.title }}</v-list-item-title>
            <v-list-item-subtitle>#{{ issue.id }}</v-list-item-subtitle>
          </v-list-item>
          <v-list-item
            v-for="n in (pageInfo?.size || 0) - (issues?.length || 0)"
            :key="'invisible-' + n"
          ></v-list-item>
        </v-list>
      </v-col>
    </v-row>
    <v-row v-if="pageInfo">
      <v-col
        cols="12"
        md="3"
        sm="6"
      >
        <v-pagination
          :length="pageInfo.totalPages"
          :model-value="pageInfo.number + 1"
          :total-visible="5"
          @update:model-value="navigatePage"
        ></v-pagination>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type Issue from "@/types/Issue.ts";
import type { Page, PageInfo } from "@/types/Page.ts";

import { onMounted, ref } from "vue";

import { getIssueList } from "@/api/fetch-issuelist.ts";
import { ROUTES_ISSUES_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const issues = ref<Issue[]>();
const pageInfo = ref<PageInfo>();

onMounted(() => {
  fetchIssues(1, 10);
});

function fetchIssues(pageNumber: number, pageSize: number) {
  getIssueList(pageNumber - 1, pageSize)
    .then((content: Page<Issue>) => {
      issues.value = content.content;
      pageInfo.value = content.page;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function navigatePage(newPage: number) {
  if (!pageInfo.value || !newPage) return;
  fetchIssues(newPage, pageInfo.value.size);
}

function goToIssue(id: number) {
  router.push({ name: ROUTES_ISSUES_DETAIL, params: { id } });
}
</script>
