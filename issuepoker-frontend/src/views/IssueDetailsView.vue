<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1 v-if="issue">{{ issue.title }}</h1>
        <h1 v-else>Issue wurde nicht gefunden</h1>
      </v-col>
      <v-col cols="auto">
        <v-btn :to="{ name: ROUTES_HOME }">Zurück zur Liste</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <p v-if="issue">{{ issue.description }}</p>
        <p v-else>Bitte zurück zur Liste gehen.</p>
      </v-col>
    </v-row>
    <v-row v-if="issue">
      <v-col>
        <issue-voting :issue="issue" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import IssueVoting from "@/components/IssueVoting.vue";
import { ROUTES_HOME, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

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
