<template>
  <v-container>
    <h1>Issue Liste</h1>
    <v-list lines="three">
      <v-list-item
        v-for="issue in issues"
        :key="issue.id"
        @click="goToIssue(issue.id)"
      >
        <v-list-item-title>{{ issue.title }}</v-list-item-title>
        <v-list-item-subtitle>{{ issue.description }}</v-list-item-subtitle>
      </v-list-item>
    </v-list>
  </v-container>
</template>

<script lang="ts" setup>
import type Issue from "@/types/Issue.ts";

import { ROUTES_ISSUES_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";

const issues: Issue[] = [
  { id: 1, title: "Issue 1", description: "Beschreibung für Issue 1" },
  { id: 2, title: "Issue 2", description: "Beschreibung für Issue 2" },
];

function goToIssue(id: number) {
  router.push({ name: ROUTES_ISSUES_DETAIL, params: { id } });
}
</script>
