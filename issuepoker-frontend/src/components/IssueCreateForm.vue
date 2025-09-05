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
          label="Besitzer"
        />
      </v-col>
      <v-col>
        <v-text-field
          v-model="repository"
          :disabled="!keyChangeable"
          :rules="[validateRepository]"
          label="Repository"
        />
      </v-col>
      <v-col>
        <v-number-input
          v-model="number"
          :disabled="!keyChangeable"
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
import { useSaveLeave } from "@/composables/saveLeave.ts";
import { ROUTES_HOME, ROUTES_ISSUE_DETAIL } from "@/constants.ts";
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
    description.value
  )
    .then((content: IssueDetails) => {
      saved.value = true;
      const { owner, repository, number } = content;
      router.push({
        name: ROUTES_ISSUE_DETAIL,
        params: { owner, repository, number },
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
    description.value,
  ];
  const originalValues = [
    originalIssue?.owner ?? "",
    originalIssue?.repository ?? "",
    originalIssue?.number ?? null,
    originalIssue?.title ?? "",
    originalIssue?.description ?? "",
  ];
  return currentValues.some((value, index) => value !== originalValues[index]);
}
</script>
