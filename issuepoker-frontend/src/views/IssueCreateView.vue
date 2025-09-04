<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1 v-if="action === 'edit'">Issue Bearbeiten</h1>
        <h1 v-else>Neues Issue Erstellen</h1>
      </v-col>
      <v-col cols="auto">
        <v-dialog max-width="500">
          <template v-slot:activator="{ props: activatorProps }">
            <v-btn
              :prepend-icon="mdiImport"
              v-bind="activatorProps"
              >Importieren
            </v-btn>
          </template>
          <template v-slot:default="{ isActive }">
            <issue-import-form
              :isActive="isActive"
              :issue="issue"
            />
          </template>
        </v-dialog>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <issue-create-form
          :action="action ?? 'new'"
          :issue="issue"
          :keyChangeable="action === undefined"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type { RouteParamsGeneric } from "vue-router";

import { mdiImport } from "@mdi/js";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/issue/get-issue.ts";
import IssueCreateForm from "@/components/IssueCreateForm.vue";
import IssueImportForm from "@/components/IssueImportForm.vue";
import { ROUTES_ISSUE_EDIT, ROUTES_ISSUE_NEW } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useIssueImportStore } from "@/stores/issueImport.ts";
import { parseRouteParamsToIssueKey } from "@/util/parser.ts";

type Action = "edit" | "new" | undefined;

const issueImportStore = useIssueImportStore();
const route = useRoute();
const issue = ref<IssueDetails>();
const action = ref<Action>();

onMounted(() => {
  fetchIssue(route.params);
});

watch(
  () => route.params,
  (params) => fetchIssue(params)
);

watch(
  () => issueImportStore.getIssueImport,
  (imported) => importIssue(imported)
);

function fetchIssue(params: RouteParamsGeneric) {
  action.value = route.params.action as Action;
  importIssue(issueImportStore.getIssueImport);
  if (route.name === ROUTES_ISSUE_NEW || issue.value) return;
  const { owner, repository, number } = parseRouteParamsToIssueKey(params);
  getIssue(owner, repository, number)
    .then((content: IssueDetails) => {
      router.push({
        name: ROUTES_ISSUE_EDIT,
        params: { owner, repository, number, action: "edit" },
      });
      issue.value = content;
    })
    .catch(() => {
      router.push({
        name: ROUTES_ISSUE_EDIT,
        params: { owner, repository, number, action: "new" },
      });
      issue.value = {
        owner,
        repository,
        number,
        title: "",
        description: "",
      };
    });
}

function importIssue(imported: IssueDetails | null) {
  if (imported) {
    issue.value = imported;
    issueImportStore.setIssueImport(null);
  }
}
</script>
