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

import { ROUTES_ISSUES_LIST } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const issue = ref<Issue>();
const route = useRoute();
const router = useRouter();

onMounted(() => {
  getIssue(route.params.id);
});

watch(
  () => route.params.id,
  (newId) => getIssue(newId)
);

function getIssue(id: string | string[]) {
  try {
    if (Array.isArray(id)) {
      id = id[0];
    }
    fetchIssue(parseInt(id));
  } catch (error) {
    snackbarStore.showMessage({ message: (error as Error).message });
    goBack();
  }
}

function fetchIssue(id: number) {
  const issues: Issue[] = [
    { id: 1, title: "Issue 1", description: "Beschreibung für Issue 1" },
    { id: 2, title: "Issue 2", description: "Beschreibung für Issue 2" },
  ];
  const foundIssue = issues.find((issue) => issue.id === id);
  if (!foundIssue) {
    throw new Error(`Issue mit ID ${id} wurde nicht gefunden.`);
  }
  issue.value = foundIssue;
}

function goBack() {
  router.push({ name: ROUTES_ISSUES_LIST });
}
</script>
