<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <h1>Neues Issue Erstellen</h1>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <issue-create-form></issue-create-form>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import IssueCreateForm from "@/components/IssueCreateForm.vue";
</script>
