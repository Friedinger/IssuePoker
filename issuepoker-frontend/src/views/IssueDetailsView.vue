<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <template v-if="issue">
          <h1>{{ issue.title }}</h1>
          <p>{{ issue.owner }}/{{ issue.repository }} #{{ issue.id }}</p>
        </template>
      </v-col>
      <v-col cols="auto">
        <v-btn :to="{ name: ROUTES_HOME }">Zurück zur Liste</v-btn>
      </v-col>
    </v-row>
    <template v-if="issue">
      <v-row>
        <v-col>
          <p>{{ issue.description }}</p>
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

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import IssueVoting from "@/components/IssueVoting.vue";
import { ROUTES_HOME, STATUS_INDICATORS } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import router from "@/plugins/router.ts";

const snackbarStore = useSnackbarStore();
const route = useRoute();
const issue = ref<IssueDetails>();

onMounted(() => {
  fetchIssue(route.params.id);
});

watch(
  () => route.params.id,
  (newId) => fetchIssue(newId)
);

function fetchIssue(id: string | string[]) {
  const parsedId = Array.isArray(id) ? parseInt(id[0]) : parseInt(id);
  getIssue(parsedId)
    .then((content: IssueDetails) => (issue.value = content))
    .catch(() => {
      snackbarStore.showMessage({
        message: `Issue mit ID "${id}" wurde nicht gefunden.`,
        level: STATUS_INDICATORS.WARNING,
      });
      router.push({ name: ROUTES_HOME });
    });
}
</script>
