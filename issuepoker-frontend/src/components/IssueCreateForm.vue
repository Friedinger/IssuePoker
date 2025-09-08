<template>
  <v-form
    v-model="valid"
    @submit.prevent
  >
    <v-row>
      <v-col>
        <v-text-field
          v-model="owner"
          :disabled="!keyChangeable"
          :rules="[validateOwner]"
          hide-details="auto"
          label="Besitzer"
        />
      </v-col>
      <v-col>
        <v-text-field
          v-model="repository"
          :disabled="!keyChangeable"
          :rules="[validateRepository]"
          hide-details="auto"
          label="Repository"
        />
      </v-col>
      <v-col>
        <v-number-input
          v-model="number"
          :disabled="!keyChangeable"
          :min="1"
          :rules="[validateNumber]"
          hide-details="auto"
          label="Nummer"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-text-field
          v-model="title"
          :rules="[validateTitle]"
          hide-details="auto"
          label="Titel"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <label-input
          v-model="labels"
          hide-details="auto"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-textarea
          v-model="description"
          :rules="[validateDescription]"
          hide-details="auto"
          label="Beschreibung"
        />
      </v-col>
    </v-row>
    <v-row align="center">
      <v-col cols="auto">
        <v-btn
          :disabled="!valid || (!isDirty() && action !== 'new')"
          :prepend-icon="mdiContentSave"
          type="submit"
          @click="save"
          >Speichern
        </v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn
          :prepend-icon="mdiCancel"
          :to="cancel()"
          >Abbrechen
        </v-btn>
      </v-col>
    </v-row>
  </v-form>
  <yes-no-dialog
    v-model="saveLeaveDialog"
    :dialogtext="saveLeaveDialogText"
    :dialogtitle="saveLeaveDialogTitle"
    @no="stay"
    @yes="leave"
  />
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";
import type { RouteLocationRaw } from "vue-router";

import { mdiCancel, mdiContentSave } from "@mdi/js";
import { onMounted, ref, watch } from "vue";

import { createIssue } from "@/api/issue/create-issue.ts";
import { updateIssue } from "@/api/issue/update-issue.ts";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import LabelInput from "@/components/LabelInput.vue";
import { useSaveLeave } from "@/composables/saveLeave.ts";
import {
  ROUTES_HOME,
  ROUTES_ISSUE_DETAIL,
  STATUS_INDICATORS,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const {
  cancel: stay,
  leave,
  saveLeaveDialog,
  saveLeaveDialogText,
  saveLeaveDialogTitle,
} = useSaveLeave(isDirty);

const owner = ref("");
const repository = ref("");
const number = ref(NaN);
const title = ref("");
const labels = ref<Record<string, string>>({});
const description = ref("");
const valid = ref();
const saved = ref<boolean>(false);

const {
  issue,
  originalIssue,
  keyChangeable = true,
  action,
} = defineProps<{
  issue?: IssueDetails;
  originalIssue?: IssueDetails;
  keyChangeable?: boolean;
  action: "new" | "edit";
}>();

onMounted(() => {
  parseProp(issue);
});

watch(
  () => issue,
  (issue) => parseProp(issue)
);

function save() {
  if (!valid.value) return;
  const request = action === "edit" ? updateIssue : createIssue;
  request(
    owner.value,
    repository.value,
    number.value,
    title.value,
    description.value,
    labels.value
  )
    .then((content: IssueDetails) => {
      saved.value = true;
      const { owner, repository, number } = content;
      router.push({
        name: ROUTES_ISSUE_DETAIL,
        params: { owner, repository, number },
      });
      snackbarStore.showMessage({
        message: `${owner}/${repository}#${number} wurde erfolgreich ${action === "edit" ? "bearbeitet" : "angelegt"}.`,
        level: STATUS_INDICATORS.SUCCESS,
      });
    })
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

function validateNumber(value: number | null) {
  if (value === null) return "Bitte eine Nummer angeben.";
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
  labels.value = issue.labels;
  description.value = issue.description;
}

function cancel(): RouteLocationRaw {
  if (action === "edit" && issue) {
    const { owner, repository, number } = issue;
    return {
      name: ROUTES_ISSUE_DETAIL,
      params: { owner, repository, number },
    };
  }
  return { name: ROUTES_HOME };
}

function isDirty(): boolean {
  if (saved.value === true) return false;
  const currentValues = [
    owner.value,
    repository.value,
    number.value,
    title.value,
    labels.value,
    description.value,
  ];
  const originalValues = [
    originalIssue?.owner ?? "",
    originalIssue?.repository ?? "",
    originalIssue?.number ?? null,
    originalIssue?.title ?? "",
    originalIssue?.labels ?? {},
    originalIssue?.description ?? "",
  ];
  return currentValues.some((value, index) => value !== originalValues[index]);
}
</script>
