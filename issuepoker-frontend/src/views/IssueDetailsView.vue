<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <template v-if="issue">
          <h1>{{ issue.title }}</h1>
          <p>{{ issue.owner }}/{{ issue.repository }} #{{ issue.number }}</p>
        </template>
      </v-col>
      <issue-details-actions :issue="issue" />
    </v-row>
    <template v-if="issue">
      <v-row>
        <v-col>
          <vue-markdown
            :options="markdownOptions"
            :source="issue.description"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <issue-voting :issue="issue" />
        </v-col>
      </v-row>
    </template>
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
