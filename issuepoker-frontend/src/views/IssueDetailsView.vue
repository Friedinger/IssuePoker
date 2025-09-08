<template>
  <v-container>
    <v-row align="center">
      <v-col v-if="issue">
        <h1>{{ issue.title }}</h1>
        <p>{{ issue.owner }}/{{ issue.repository }} #{{ issue.number }}</p>
      </v-col>
      <issue-details-actions :issue="issue" />
    </v-row>
    <v-row v-if="issue">
      <v-col
        cols="12"
        lg="6"
      >
        <v-card>
          <v-card-text>
            <vue-markdown
              :options="markdownOptions"
              :source="issue.description"
            />
          </v-card-text>
        </v-card>
      </v-col>
      <v-col
        cols="12"
        lg="6"
      >
        <v-card>
          <v-card-title>Abstimmen</v-card-title>
          <v-card-text><issue-voting :issue="issue" /></v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type { RouteParamsGeneric } from "vue-router";

import { onMounted, ref, watch } from "vue";
import VueMarkdown from "vue-markdown-render";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/issue/get-issue.ts";
import IssueDetailsActions from "@/components/IssueDetailsActions.vue";
import IssueVoting from "@/components/IssueVoting.vue";
import {
  ROUTES_HOME,
  ROUTES_ISSUE_EDIT,
  STATUS_INDICATORS,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useIssueImportStore } from "@/stores/issueImport.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { parseRouteParamsToIssueKey } from "@/util/parser.ts";
import { isAdmin } from "@/util/userUtils.ts";

const markdownOptions = {
  html: true,
};
const snackbarStore = useSnackbarStore();
const issueImportStore = useIssueImportStore();
const route = useRoute();
const issue = ref<IssueDetails>();

onMounted(() => {
  fetchIssue(route.params);
});

watch(
  () => route.params,
  (params) => fetchIssue(params)
);

function fetchIssue(params: RouteParamsGeneric) {
  const { owner, repository, number } = parseRouteParamsToIssueKey(params);
  getIssue(owner, repository, number)
    .then((content: IssueDetails) => (issue.value = content))
    .catch(() => {
      if (isAdmin()) {
        issueImportStore.setIssueImport({
          owner,
          repository,
          number,
          title: "",
          description: "",
        });
        snackbarStore.showMessage({
          message: `${owner}/${repository}#${number} wurde nicht gefunden. Ein neues Issue kann jetzt erstellt werden.`,
          level: STATUS_INDICATORS.INFO,
        });
        router.push({
          name: ROUTES_ISSUE_EDIT,
          params: { owner, repository, number, action: "new" },
        });
      } else {
        snackbarStore.showMessage({
          message: `${owner}/${repository}#${number} wurde nicht gefunden.`,
          level: STATUS_INDICATORS.WARNING,
        });
        router.push({ name: ROUTES_HOME });
      }
    });
}
</script>

<style>
ul,
ol {
  margin-left: 1.25rem;
}
</style>
