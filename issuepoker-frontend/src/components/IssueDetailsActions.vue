<template>
  <v-col
    v-if="isAdmin() && issue"
    cols="auto"
  >
    <v-tooltip
      location="top"
      text="Issue bearbeiten"
    >
      <template v-slot:activator="{ props }">
        <v-btn
          :icon="mdiPencil"
          :to="{
            name: ROUTES_ISSUE_EDIT,
            params: {
              owner: issue.owner,
              repository: issue.repository,
              number: issue.number,
              action: 'edit',
            },
          }"
          density="comfortable"
          rounded="rounded"
          v-bind="props"
        />
      </template>
    </v-tooltip>
  </v-col>
  <v-col
    v-if="isAdmin() && issue"
    cols="auto"
  >
    <v-tooltip
      location="top"
      text="Issue löschen"
    >
      <template v-slot:activator="{ props }">
        <v-btn
          :icon="mdiDelete"
          density="comfortable"
          rounded="rounded"
          v-bind="props"
          @click="deleteDialog = true"
        />
      </template>
    </v-tooltip>
    <v-dialog
      v-model="deleteDialog"
      max-width="500"
    >
      <template v-slot:default>
        <issue-delete
          :issue="issue"
          @close="deleteDialog = false"
        />
      </template>
    </v-dialog>
  </v-col>
  <v-col cols="auto">
    <v-btn :to="{ name: ROUTES_HOME }">Zurück zur Liste</v-btn>
  </v-col>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import { mdiDelete, mdiPencil } from "@mdi/js";
import { ref } from "vue";

import IssueDelete from "@/components/IssueDelete.vue";
import { ROUTES_HOME, ROUTES_ISSUE_EDIT } from "@/constants.ts";
import { isAdmin } from "@/util/userUtils.ts";

const { issue } = defineProps<{
  issue: IssueDetails | undefined;
}>();
const deleteDialog = ref(false);
</script>
