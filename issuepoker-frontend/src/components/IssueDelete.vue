<template>
  <v-card title="Issue Löschen">
    <v-card-text>
      Das folgende Issue wird gelöscht:<br />
      <br />
      <strong>Besitzer:</strong> {{ issue.owner }}<br />
      <strong>Repository:</strong> {{ issue.repository }}<br />
      <strong>Nummer:</strong> {{ issue.number }}<br />
      <strong>Titel:</strong> {{ issue.title }}<br />
      <br />
      Sind Sie sicher, dass Sie dieses Issue löschen möchten?
    </v-card-text>
    <v-card-actions>
      <v-btn
        :prepend-icon="mdiDelete"
        @click="handleDeleteIssue"
        >Löschen
      </v-btn>
      <v-spacer />
      <v-btn
        :prepend-icon="mdiCancel"
        @click="emit('close')"
        >Abbrechen
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import { mdiCancel, mdiDelete } from "@mdi/js";

import { deleteIssue } from "@/api/delete-issue.ts";
import { ROUTES_HOME, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const props = defineProps<{
  issue: IssueDetails;
}>();
const emit = defineEmits<(e: "close") => void>();

function handleDeleteIssue() {
  deleteIssue(props.issue)
    .then(() => {
      router.push({ name: ROUTES_HOME });
      snackbarStore.showMessage({
        message: "Issue erfolgreich gelöscht",
        level: STATUS_INDICATORS.SUCCESS,
      });
      emit("close");
    })
    .catch((error) => snackbarStore.showMessage(error));
}
</script>
