<template>
  <v-container>
    <template v-if="issue">
      <h1>{{ issue.title }}</h1>
      <p>{{ issue.description }}</p>
    </template>
    <template v-else>
      <h1>Issue nicht gefunden</h1>
    </template>
    <v-btn @click="goBack">Zurück zur Liste</v-btn>
  </v-container>
</template>

<script lang="ts" setup>
import type Issue from "@/types/Issue.ts";

import { onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import { ROUTES_ISSUES_LIST } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const issue = ref<Issue>();
const route = useRoute();
const router = useRouter();

onMounted(() => {
  fetchIssue(parseId(route.params.id));
});

watch(
  () => route.params.id,
  (newId) => fetchIssue(parseId(newId))
);

function fetchIssue(id: number) {
  getIssue(id)
    .then((content: Issue) => (issue.value = content))
    .catch((error) => {
      snackbarStore.showMessage(
        new Error(`Issue mit ID ${id} wurde nicht gefunden: ${error}`)
      );
    });
}

function parseId(id: string | string[]): number {
  if (Array.isArray(id)) {
    id = id[0];
  }
  return parseInt(id);
}

function goBack() {
  router.push({ name: ROUTES_ISSUES_LIST });
}
</script>
