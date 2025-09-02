<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <template v-if="issue">
          <h1>{{ issue.title }}</h1>
          <p>{{ issue.owner }}/{{ issue.repository }} #{{ issue.number }}</p>
        </template>
      </v-col>
      <v-col
        v-if="issue"
        cols="auto"
      >
        <v-tooltip
          location="top"
          text="Issue bearbeiten"
        >
          <template v-slot:activator="{ props }">
            <v-btn
              :icon="mdiPencil"
              :to="{
                name: ROUTES_ISSUE_EDIT,
                params: {
                  owner: issue.owner,
                  repository: issue.repository,
                  number: issue.number,
                  action: 'edit',
                },
              }"
              density="comfortable"
              rounded="rounded"
              v-bind="props"
            />
          </template>
        </v-tooltip>
      </v-col>
      <v-col cols="auto">
        <v-btn :to="{ name: ROUTES_HOME }">Zurück zur Liste</v-btn>
      </v-col>
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

import { mdiPencil } from "@mdi/js";
import { onMounted, ref, watch } from "vue";
import VueMarkdown from "vue-markdown-render";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import IssueVoting from "@/components/IssueVoting.vue";
import {
  ROLE_ADMIN,
  ROUTES_HOME,
  ROUTES_ISSUE_EDIT,
  STATUS_INDICATORS,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";

const snackbarStore = useSnackbarStore();
const route = useRoute();
const issue = ref<IssueDetails>();
const markdownOptions = {
  html: true,
};

onMounted(() => {
  fetchIssue(route.params);
});

watch(
  () => route.params,
  (params) => fetchIssue(params)
);

function fetchIssue(params: RouteParamsGeneric) {
  const { owner, repository, number } = parseParams(params);
  getIssue(owner, repository, number)
    .then((content: IssueDetails) => (issue.value = content))
    .catch(() => {
      if (useUserStore().getUser?.authorities.includes(ROLE_ADMIN)) {
        router.push({
          name: ROUTES_ISSUE_EDIT,
          params: { owner, repository, number, action: "new" },
        });
      } else {
        snackbarStore.showMessage({
          message: `Issue "${owner}/${repository}#${number}" wurde nicht gefunden.`,
          level: STATUS_INDICATORS.WARNING,
        });
        router.push({ name: ROUTES_HOME });
      }
    });
}

function parseParams(params: RouteParamsGeneric): {
  owner: string;
  repository: string;
  number: number;
} {
  const owner = params.owner;
  const repository = params.repository;
  const number = params.number;
  return {
    owner: Array.isArray(owner) ? owner[0] : owner,
    repository: Array.isArray(repository) ? repository[0] : repository,
    number: Array.isArray(number) ? parseInt(number[0]) : parseInt(number),
  };
}
</script>

<style>
ul,
ol {
  margin-left: 1.25rem;
}
</style>
