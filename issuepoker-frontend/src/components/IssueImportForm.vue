<template>
  <v-card title="Issue importieren">
    <v-form
      v-model="valid"
      @submit.prevent
    >
      <v-card-text>
        <v-text-field
          v-model="url"
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
          >Importieren</v-btn
        >
        <v-spacer />
        <v-btn
          :prepend-icon="mdiCancel"
          @click="isActive = false"
          >Abbrechen</v-btn
        >
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script lang="ts" setup>
import { mdiCancel, mdiImport } from "@mdi/js";
import { ref } from "vue";

import { ROUTES_ISSUE_CREATE } from "@/constants.ts";
import router from "@/plugins/router.ts";

const props = defineProps({ isActive: Boolean });
const isActive = ref(props.isActive);
const url = ref("");
const valid = ref(false);
const issueUrlRegex = /^https:\/\/github\.com\/[\w-]+\/[\w-]+\/issues\/\d+$/;

function validateUrl(value: string) {
  if (value.trim().length < 1) return "Bitte eine URL angeben.";
  if (!issueUrlRegex.test(value)) {
    return "Bitte eine gültige GitHub Issue URL angeben.";
  }
  return true;
}

function importIssue() {
  if (!valid.value) return;
  const match = url.value.match(
    /https:\/\/github\.com\/([\w-]+)\/([\w-]+)\/issues\/(\d+)/
  );
  if (!match) return;
  const owner = match[1];
  const repository = match[2];
  const number = parseInt(match[3]);
  if (isNaN(number)) return;
  router.push({
    name: ROUTES_ISSUE_CREATE,
    query: { owner, repository, number },
  });
  isActive.value = false;
}
</script>

<style scoped></style>
