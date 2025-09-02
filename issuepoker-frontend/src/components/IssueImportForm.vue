<template>
  <v-card title="Issue importieren">
    <v-form
      v-model="valid"
      @submit.prevent
    >
      <v-card-text>
        <v-text-field
          v-model="url"
          :disabled="issue !== undefined"
          :rules="[validateUrl]"
          label="GitHub URL"
        />
      </v-card-text>
      <v-card-actions>
        <v-btn
          :disabled="!valid"
          :prepend-icon="mdiImport"
          type="submit"
          @click="importIssue"
          >Importieren
        </v-btn>
        <v-spacer />
        <v-btn
          :prepend-icon="mdiCancel"
          @click="isActive = false"
          >Abbrechen
        </v-btn>
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type { IssueRemote } from "@/types/IssueRemote.ts";
import type { Ref } from "vue";

import { mdiCancel, mdiImport } from "@mdi/js";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import { getIssueRemote } from "@/api/fetch-issues-remote.ts";
import { ROUTES_ISSUE_EDIT, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const issueUrlRegex =
  /^https:\/\/github\.com\/([\w-]+)\/([\w-]+)\/issues\/(\d+)$/;
const snackbarStore = useSnackbarStore();
const route = useRoute();

const props = defineProps<{
  isActive: Ref<boolean, boolean>;
  issue?: IssueDetails;
}>();
const emit = defineEmits<(e: "return", value: IssueDetails) => void>();
const isActive = ref(props.isActive);
const issue = ref<IssueDetails | undefined>(props.issue);
const url = ref("");
const valid = ref(false);

onMounted(() => {
  issueToUrl();
});

function importIssue() {
  if (!valid.value) return;
  const match = url.value.match(issueUrlRegex);
  if (!match) return;
  const [, owner, repository, number] = match;
  getIssueRemote(owner, repository, parseInt(number))
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
      if (route.name !== ROUTES_ISSUE_EDIT) {
        router.push({
          name: ROUTES_ISSUE_EDIT,
          params: { owner, repository, number, action: "new" },
        });
      }
      emit("return", issue.value);
      isActive.value = false;
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
      issue.value = undefined;
    });
}

function issueToUrl() {
  if (!issue.value) return;
  url.value = `https://github.com/${issue.value.owner}/${issue.value.repository}/issues/${issue.value.number}`;
}

function validateUrl(value: string) {
  if (value.trim().length < 1) return "Bitte eine URL angeben.";
  if (!issueUrlRegex.test(value)) {
    return "Bitte eine gültige GitHub Issue URL angeben.";
  }
  return true;
}
</script>

<style scoped></style>
