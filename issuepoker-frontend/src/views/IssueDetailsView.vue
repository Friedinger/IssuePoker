<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1>{{ issue.title }}</h1>
      </v-col>
      <v-col cols="auto">
        <v-btn :to="{ name: ROUTES_HOME }">Zurück zur Liste</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <p>{{ issue.description }}</p>
      </v-col>
    </v-row>
    <v-row v-if="validIssueLoaded()">
      <v-col>
        <issue-voting :issue="issue" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type Issue from "@/types/Issue.ts";

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import IssueVoting from "@/components/IssueVoting.vue";
import { ROUTES_HOME, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const issueLoading: Issue = {
  id: NaN,
  title: "Issue wird geladen...",
  description: "Bitte warten.",
  votes: [],
};
const issueNotFound: Issue = {
  id: NaN,
  title: "Issue wurde nicht gefunden",
  description: "Bitte zurück zur Liste gehen.",
  votes: [],
};

const snackbarStore = useSnackbarStore();
const route = useRoute();
const issue = ref<Issue>(issueLoading);

onMounted(() => {
  fetchIssue(route.params.id);
});

watch(
  () => route.params.id,
  (newId) => fetchIssue(newId)
);

function fetchIssue(id: string | string[]) {
  getIssue(parseId(id))
    .then((content: Issue) => (issue.value = content))
    .catch(() => {
      snackbarStore.showMessage({
        message: `Issue mit ID "${id}" wurde nicht gefunden.`,
        level: STATUS_INDICATORS.WARNING,
      });
      issue.value = issueNotFound;
      router.push({ name: ROUTES_HOME });
    });
}

function parseId(id: string | string[]): number {
  if (Array.isArray(id)) {
    id = id[0];
  }
  return parseInt(id);
}

function validIssueLoaded(): boolean {
  const invalidIssues = [issueLoading, issueNotFound];
  return !invalidIssues.includes(issue.value);
}
</script>
