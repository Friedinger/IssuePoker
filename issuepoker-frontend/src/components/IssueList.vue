<template>
  <v-row>
    <issue-list-filters @fetchIssues="fetchIssues" />
  </v-row>
  <v-row>
    <v-data-table-server
      :headers="headers"
      :hover="true"
      :items="issues"
      :items-length="totalIssues"
      :items-per-page="itemsPerPage"
      :items-per-page-options="itemsPerPageOptions"
      :loading="loading"
      :page="page"
      :search="search"
      :sort-by="sortBy"
      multi-sort
      @update:options="updateOptions"
      @click:row="goToIssue"
    />
  </v-row>
</template>

<script lang="ts" setup>
import IssueListFilters from "@/components/IssueListFilters.vue";
import { useIssueList } from "@/composables/issueList.ts";

const headers = [
  { key: "owner", title: "Besitzer" },
  { key: "repository", title: "Repository" },
  { key: "number", title: "Nummer" },
  { key: "title", title: "Titel" },
  { key: "voteCount", title: "Stimmen", sortable: false },
  { key: "voteResult", title: "Ergebnis", sortable: false },
];
const itemsPerPageOptions = [
  { value: 5, title: "5" },
  { value: 10, title: "10" },
  { value: 25, title: "25" },
  { value: 50, title: "50" },
  { value: 100, title: "100" },
  { value: -1, title: "$vuetify.dataFooter.itemsPerPageAll" },
];

const {
  issues,
  totalIssues,
  itemsPerPage,
  loading,
  page,
  search,
  sortBy,
  fetchIssues,
  updateOptions,
  goToIssue,
} = useIssueList();
</script>
