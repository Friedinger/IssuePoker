<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <v-img
          class="my-3"
          height="200"
          src="@/assets/IssuePoker_Logo_Light.svg"
        />
      </v-col>
      <v-col>
        <h1 class="text-h3 font-weight-bold mb-3">
          Willkommen beim Issue Poker
        </h1>
        <p>
          Stimme für Issues ab, um im Team die Komplexität und den Aufwand der
          Aufgaben für Scrum zu schätzen.
        </p>
      </v-col>
    </v-row>
    <v-row align="center">
      <v-col><h1>Issues</h1></v-col>
      <v-col
        v-if="isAdmin()"
        cols="auto"
      >
        <v-btn
          :prepend-icon="mdiNotePlus"
          :to="{ name: ROUTES_ISSUE_NEW }"
          >Neues Issue
        </v-btn>
      </v-col>
      <v-col cols="auto">
        <issue-import-form button-text="Issue Importieren" />
      </v-col>
    </v-row>
    <issue-list />
  </v-container>
</template>

<script lang="ts" setup>
import { mdiNotePlus } from "@mdi/js";

import IssueImportForm from "@/components/IssueImportForm.vue";
import IssueList from "@/components/IssueList.vue";
import { ROUTES_ISSUE_NEW } from "@/constants.ts";
import { isAdmin } from "@/util/userUtils.ts";
</script>
