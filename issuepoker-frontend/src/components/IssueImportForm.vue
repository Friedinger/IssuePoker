<template>
  <v-btn
    :prepend-icon="mdiImport"
    @click="open"
    >{{ buttonText ?? "Importieren" }}
  </v-btn>

  <v-dialog
    v-model="active"
    max-width="500"
  >
    <template v-slot:default>
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
              @click="runImport"
              >Importieren
            </v-btn>
            <v-spacer />
            <v-btn
              :prepend-icon="mdiCancel"
              @click="active = false"
              >Abbrechen
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </template>
  </v-dialog>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import { mdiCancel, mdiImport } from "@mdi/js";
import { onMounted, ref, watch } from "vue";
import { VForm } from "vuetify/components";

import { useIssueImport } from "@/composables/issueImport.ts";

const props = defineProps<{
  buttonText?: string;
  issue?: IssueDetails;
}>();

const active = ref<boolean>(false);
const issue = ref<IssueDetails | undefined>(props.issue);

const { url, valid, importIssue, validateUrl } = useIssueImport();

onMounted(() => {
  url.value = issueToUrl(issue.value);
});

watch(
  () => props.issue,
  (prop) => {
    issue.value = prop;
    url.value = issueToUrl(issue.value);
  }
);

async function open() {
  if (validateUrl(url.value) === true) {
    importIssue();
  } else {
    active.value = true;
  }
}

function runImport() {
  importIssue();
  active.value = false;
}

function issueToUrl(issue: IssueDetails | undefined) {
  if (!issue) return "";
  return `https://github.com/${issue.owner}/${issue.repository}/issues/${issue.number}`;
}
</script>
