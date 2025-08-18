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
        <h1 class="text-h3 font-weight-bold mb-3 text-center">
          Willkommen beim Issue Poker
        </h1>
        <p>
          Stimme für Issues ab, um im Team die Komplexität und den Aufwand der
          Aufgaben für Scrum zu schätzen.
        </p>
      </v-col>
    </v-row>
    <issue-list />
  </v-container>
</template>

<script lang="ts" setup>
import IssueList from "@/components/IssueList.vue";
</script>
