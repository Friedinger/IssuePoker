<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1 v-if="action === 'edit'">Issue Bearbeiten</h1>
        <h1 v-else>Neues Issue Erstellen</h1>
      </v-col>
      <v-col cols="auto">
        <issue-import-form :issue="issue" />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <issue-create-form
          :action="action ?? 'new'"
          :issue="issue"
          :keyChangeable="action === undefined"
          :originalIssue="originalIssue"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueKey from "@/types/IssueKey.ts";
import type { RouteParamsGeneric } from "vue-router";

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import IssueCreateForm from "@/components/IssueCreateForm.vue";
import IssueImportForm from "@/components/IssueImportForm.vue";
import { useIssueCreate } from "@/composables/issueCreate.ts";
import { parseRouteParamsToIssueKey } from "@/util/parser.ts";

type Action = "edit" | "new" | undefined;

const route = useRoute();
const action = ref<Action>(route.params.action as Action);
const issueKey = ref<IssueKey>();
const { issue, originalIssue } = useIssueCreate(issueKey);

onMounted(() => {
  parseParams(route.params);
});

watch(
  () => route.params,
  (params) => parseParams(params)
);

function parseParams(params: RouteParamsGeneric) {
  action.value = params.action as Action;
  issueKey.value = parseRouteParamsToIssueKey(params);
}
</script>
