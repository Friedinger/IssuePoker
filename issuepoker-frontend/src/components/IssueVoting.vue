<template>{{ votes }}</template>

<script lang="ts" setup>
import type Vote from "@/types/Vote.ts";

import { ref, watch } from "vue";

import { getVotes } from "@/api/fetch-votes.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();
const props = defineProps(["issue"]);
const votes = ref<Vote[]>([]);

watch(
  () => props.issue,
  () => fetchVotes()
);

function fetchVotes() {
  getVotes(props.issue.id)
    .then((content: Vote[]) => (votes.value = content))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}
</script>
