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
            <v-btn :prepend-icon="mdiImport" v-bind="activatorProps">Importieren</v-btn>
          </template>

          <template v-slot:default="{ isActive }">
            <issue-import-form
              :isActive="isActive"
              :issue="issue"
              @return="setIssue"
            />
          </template>
        </v-dialog>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <issue-create-form
          :action="action === 'edit' ? 'update' : 'create'"
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

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssue } from "@/api/fetch-issue.ts";
import IssueCreateForm from "@/components/IssueCreateForm.vue";
import IssueImportForm from "@/components/IssueImportForm.vue";
import { ROUTES_ISSUE_EDIT } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { mdiImport } from "@mdi/js";

const route = useRoute();
const issue = ref<IssueDetails>();
const action = ref(route.params.action);

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
      setIssue({
        owner,
        repository,
        number,
        title: "",
        description: "",
      });
    });
}

function setIssue(content: IssueDetails) {
  issue.value = content;
}

function parseParams(params: RouteParamsGeneric): {
  owner: string;
  repository: string;
  number: number;
} {
  action.value = route.params.action;
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
