<template>
  <v-container>
    <v-row align="center">
      <v-col v-if="issue">
        <h1>{{ issue.title }}</h1>
        <p>{{ issue.owner }}/{{ issue.repository }} #{{ issue.number }}</p>
      </v-col>
      <issue-details-actions :issue="issue" />
    </v-row>
    <v-row v-if="issue && issue.labels && Object.keys(issue.labels).length > 0">
      <v-col class="d-flex ga-2">
        <v-chip
          v-for="(color, name) in issue.labels"
          :key="name"
          :color="color"
        >
          {{ name }}
        </v-chip>
      </v-col>
    </v-row>
    <v-row v-if="issue">
      <v-col
        v-if="issue.description.trim().length > 0"
        cols="12"
        lg="6"
      >
        <v-card>
          <v-card-text>
            <markdown-render :content="issue.description" />
          </v-card-text>
        </v-card>
      </v-col>
      <v-col
        cols="12"
        lg="6"
      >
        <v-card>
          <v-card-title>Abstimmen</v-card-title>
          <v-card-text><issue-voting :issue="issue" /></v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type IssueKey from "@/types/IssueKey.ts";

import { ref, watch } from "vue";
import { useRoute } from "vue-router";

import IssueDetailsActions from "@/components/IssueDetailsActions.vue";
import IssueVoting from "@/components/IssueVoting.vue";
import MarkdownRender from "@/components/MarkdownRender.vue";
import { useIssueDetails } from "@/composables/issueDetails.ts";
import { parseRouteParamsToIssueKey } from "@/util/parser.ts";

const route = useRoute();
const issueKey = ref<IssueKey>(parseRouteParamsToIssueKey(route.params));
const { issue } = useIssueDetails(issueKey);

watch(
  () => route.params,
  (params) => (issueKey.value = parseRouteParamsToIssueKey(params))
);
</script>
