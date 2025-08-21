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
import type { IssueRemote } from "@/types/IssueRemote.ts";
import type { LocationQuery } from "vue-router";

import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getIssueRemote } from "@/api/fetch-issues-remote.ts";
import IssueCreateForm from "@/components/IssueCreateForm.vue";
import { ROUTES_HOME, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const route = useRoute();
const issue = ref<IssueDetails>();

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
  getIssueRemote(owner, repository, number)
    .then((content: IssueRemote) => {
      if (content.pull_request) {
        throw new Error("Pull Requests are not supported.");
      }
      issue.value = {
        owner,
        repository,
        number: content.number,
        title: content.title,
        description: content.body || "",
      };
    })
    .catch((e) => {
      if (e.message === "Pull Requests are not supported.") {
        snackbarStore.showMessage({
          message: `"${owner}/${repository}#${number}" ist ein Pull Request und wird nicht unterstützt.`,
          level: STATUS_INDICATORS.WARNING,
        });
      } else {
        snackbarStore.showMessage({
          message: `Remote Issue "${owner}/${repository}#${number}" wurde nicht gefunden.`,
          level: STATUS_INDICATORS.WARNING,
        });
      }
      router.push({ name: ROUTES_HOME });
    });
}
</script>
