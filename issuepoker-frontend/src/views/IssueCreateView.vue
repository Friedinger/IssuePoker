<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1>Neues Issue Erstellen</h1>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <issue-create-form :issue="issue" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type { LocationQuery } from "vue-router";

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import IssueCreateForm from "@/components/IssueCreateForm.vue";

const issue = ref<IssueDetails>();
const route = useRoute();

onMounted(() => {
  fetchRemote(route.query);
});

watch(
  () => route.query,
  (query) => fetchRemote(query)
);

function fetchRemote(query: LocationQuery) {
  const owner = query.owner as string;
  const repository = query.repository as string;
  const number = parseInt(query.number as string);
  issue.value = {
    owner: owner,
    repository: repository,
    number: number,
    title: "",
    description: "",
  };
}
</script>
