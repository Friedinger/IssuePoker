<template>
  <v-row align="center">
    <v-col
      v-for="votingOption in votingOptions"
      :key="votingOption"
      cols="auto"
    >
      <v-btn
        :disabled="userVote && votingOption !== userVote.voting"
        @click="vote(votingOption)"
        >{{ votingOption }}
      </v-btn>
    </v-col>
  </v-row>
  <v-row>
    <v-col
      v-for="vote in votes"
      :key="vote.id"
    >
      <p>{{ vote.user.email }}: {{ vote.voting }}</p>
      <pre>{{ vote }}</pre>
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import type Vote from "@/types/Vote.ts";

import { ref, watch } from "vue";

import { createVote } from "@/api/create-vote.ts";
import { deleteVote } from "@/api/delete-vote.ts";
import { getVotes } from "@/api/fetch-votes.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const votingOptions = [1, 2, 3, 5, 8, 13, 21];
const userSub = "123e4567-e89b-12d3-a456-426614174006";

const snackbarStore = useSnackbarStore();
const props = defineProps(["issue"]);
const votes = ref<Vote[]>([]);
const userVote = ref<Vote>();

watch(
  () => props.issue,
  () => fetchVotes()
);

function fetchVotes() {
  getVotes(props.issue.id)
    .then((content: Vote[]) => {
      votes.value = content;
      userVote.value = content.find((v) => v.user.sub === userSub);
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function vote(voting: number) {
  if (!userVote.value) {
    createVote(props.issue.id, userSub, voting)
      .then((content: Vote) => {
        votes.value.push(content);
        userVote.value = content;
      })
      .catch((error) => snackbarStore.showMessage(error));
  } else {
    deleteVote(props.issue.id, userVote.value.id)
      .then(() => {
        userVote.value = undefined;
        fetchVotes();
      })
      .catch((error) => snackbarStore.showMessage(error));
  }
}
</script>
