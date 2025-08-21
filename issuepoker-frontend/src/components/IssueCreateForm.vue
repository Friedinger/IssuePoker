<template>
  <v-form
    v-model="valid"
    @submit.prevent
  >
    <v-row>
      <v-col>
        <v-text-field
          v-model="owner"
          :rules="[validateOwner]"
          label="Besitzer"
        />
      </v-col>
      <v-col>
        <v-text-field
          v-model="repository"
          :rules="[validateRepository]"
          label="Repository"
        />
      </v-col>
      <v-col>
        <v-number-input
          v-model="number"
          :min="1"
          :rules="[validateNumber]"
          label="Nummer"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-text-field
          v-model="title"
          :rules="[validateTitle]"
          label="Titel"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-textarea
          v-model="description"
          :rules="[validateDescription]"
          label="Beschreibung"
        />
      </v-col>
    </v-row>
    <v-row align="center">
      <v-col cols="auto">
        <v-btn
          :disabled="!valid"
          :prepend-icon="mdiContentSave"
          type="submit"
          @click="save"
          >Speichern
        </v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn
          :prepend-icon="mdiCancel"
          :to="{ name: ROUTES_HOME }"
          >Abbrechen
        </v-btn>
      </v-col>
    </v-row>
  </v-form>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import { mdiCancel, mdiContentSave } from "@mdi/js";
import { isDefined } from "@vueuse/core";
import { onMounted, ref, watch } from "vue";

import { createIssue } from "@/api/create-issue.ts";
import { ROUTES_HOME, ROUTES_ISSUE_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const owner = ref("");
const repository = ref("");
const number = ref(NaN);
const title = ref("");
const description = ref("");
const valid = ref();

const props = defineProps<{ issue?: IssueDetails }>();

onMounted(() => {
  parseProp(props.issue);
});

watch(
  () => props.issue,
  (issue) => parseProp(issue)
);

function save() {
  if (!valid.value) return;
  createIssue(
    owner.value,
    repository.value,
    number.value,
    title.value,
    description.value
  )
    .then((content: IssueDetails) =>
      router.push({
        name: ROUTES_ISSUE_DETAIL,
        params: {
          owner: content.owner,
          repository: content.repository,
          number: content.number,
        },
      })
    )
    .catch((error) => snackbarStore.showMessage(error));
}

function validateOwner(value: string) {
  if (value.trim().length < 1) return "Bitte einen Besitzer angeben.";
  if (value.length > 255)
    return "Besitzer darf nicht länger als 255 Zeichen sein.";
  return true;
}

function validateRepository(value: string) {
  if (value.trim().length < 1) return "Bitte eine Repository angeben.";
  if (value.length > 255)
    return "Repository darf nicht länger als 255 Zeichen sein.";
  return true;
}

function validateNumber(value: number) {
  if (!isDefined(number)) return "Bitte eine Nummer angeben.";
  if (value < 1) return "Nummer muss positiv sein.";
  return true;
}

function validateTitle(value: string) {
  if (value.trim().length < 1) return "Bitte einen Titel angeben.";
  if (value.length > 255)
    return "Titel darf nicht länger als 255 Zeichen sein.";
  return true;
}

function validateDescription(value: string) {
  if (value.length > 65_535)
    return "Beschreibung darf nicht länger als 65535 Zeichen sein.";
  return true;
}

function parseProp(issue?: IssueDetails) {
  if (!issue) return;
  owner.value = issue.owner;
  repository.value = issue.repository;
  number.value = issue.number;
  title.value = issue.title;
  description.value = issue.description;
}
</script>
