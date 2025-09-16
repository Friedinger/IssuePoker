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
          label="Labels"
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

import { mdiCancel, mdiContentSave } from "@mdi/js";
import { onMounted, ref, watch } from "vue";

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import LabelInput from "@/components/LabelInput.vue";
import { useIssueCreateForm } from "@/composables/issueCreateForm.ts";
import { useSaveLeave } from "@/composables/saveLeave.ts";

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

const original = ref<IssueDetails | undefined>(originalIssue);

const {
  owner,
  repository,
  number,
  title,
  labels,
  description,
  valid,
  save,
  validateOwner,
  validateRepository,
  validateNumber,
  validateTitle,
  validateDescription,
  cancel,
  isDirty,
} = useIssueCreateForm(action, issue, original);

const {
  stay,
  leave,
  saveLeaveDialog,
  saveLeaveDialogText,
  saveLeaveDialogTitle,
} = useSaveLeave(isDirty);

onMounted(() => {
  parseProp(issue);
});

watch(
  () => issue,
  (issue) => parseProp(issue)
);

watch(
  () => originalIssue,
  (originalIssue) => (original.value = originalIssue)
);

function parseProp(issue?: IssueDetails) {
  if (!issue) return;
  owner.value = issue.owner;
  repository.value = issue.repository;
  number.value = issue.number;
  title.value = issue.title;
  labels.value = issue.labels;
  description.value = issue.description;
}
</script>
