<template>
  <v-form>
    <v-row>
      <v-text-field
        v-model="title"
        label="Titel"
      ></v-text-field>
    </v-row>
    <v-row>
      <v-textarea
        v-model="description"
        label="Beschreibung"
      ></v-textarea>
    </v-row>
    <v-row align="center">
      <v-col cols="auto">
        <v-btn
          :prepend-icon="mdiContentSave"
          @click="save"
          >Speichern
        </v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn
          :prepend-icon="mdiCancel"
          :to="{ name: ROUTES_ISSUES_LIST }"
          >Abbrechen
        </v-btn>
      </v-col>
    </v-row>
  </v-form>
</template>

<script lang="ts" setup>
import { mdiCancel, mdiContentSave } from "@mdi/js";
import { ref } from "vue";

import { ROUTES_ISSUES_LIST } from "@/constants.ts";

const title = ref("");
const description = ref("");

function save() {
  description.value = title.value; // Dummy
}
</script>
