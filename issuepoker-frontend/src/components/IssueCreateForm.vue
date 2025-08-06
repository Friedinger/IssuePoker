<template>
  <v-form
    v-model="valid"
    @submit.prevent
  >
    <v-row>
      <v-text-field
        v-model="title"
        :rules="[validateTitle]"
        label="Titel"
      ></v-text-field>
    </v-row>
    <v-row>
      <v-textarea
        v-model="description"
        :rules="[validateDescription]"
        label="Beschreibung"
      ></v-textarea>
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
import type Issue from "@/types/Issue.ts";

import { mdiCancel, mdiContentSave } from "@mdi/js";
import { ref } from "vue";

import { createIssue } from "@/api/create-issue.ts";
import { ROUTES_HOME, ROUTES_ISSUE_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const title = ref("");
const description = ref("");
const valid = ref();

function save() {
  if (!valid.value) return;
  createIssue(title.value, description.value)
    .then((content: Issue) =>
      router.push({ name: ROUTES_ISSUE_DETAIL, params: { id: content.id } })
    )
    .catch((error) => snackbarStore.showMessage(error));
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
</script>
