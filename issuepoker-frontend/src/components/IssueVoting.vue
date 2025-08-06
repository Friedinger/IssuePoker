<template>
  <v-row>
    <v-col><h2>Pokern</h2></v-col>
  </v-row>
  <v-row class="mt-0">
    <v-col>
      <p v-if="!userVote">Klicke auf einen Wert um dafür zu stimmen.</p>
      <p v-else>
        Klicke auf den aktuell ausgewählten Wert, um ihn zurückzunehmen.
      </p>
    </v-col>
  </v-row>
  <v-row class="flex-nowrap overflow-x-auto">
    <v-col cols="auto">
      <v-row class="flex-nowrap">
        <v-col
          v-for="votingOption in votingOptions"
          :key="votingOption"
          cols="auto"
        >
          <v-btn
            :disabled="userVote && votingOption !== userVote.voting"
            @click="vote(votingOption)"
          >
            {{ votingOption }} <br />
          </v-btn>
          <p
            v-if="revealed && userVote"
            class="text-center"
          >
            {{ voteCounts[votingOption] ?? 0 }}x
          </p>
        </v-col>
      </v-row>
      <v-row v-if="revealed">
        <v-col>
          <v-sparkline
            v-model="voteCountValues"
            :gradient="['#FFCD00', '#334799']"
            auto-draw
            smooth="20"
            stroke-linecap="round"
          />
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="auto">
      <v-tooltip
        location="top"
        text="Ergebnisse anzeigen"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            :disabled="!userVote"
            :icon="!(revealed && userVote) ? mdiEye : mdiEyeRemove"
            v-bind="props"
            @click="revealed = !revealed"
          />
        </template>
      </v-tooltip>
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import type Vote from "@/types/Vote.ts";

import { mdiEye, mdiEyeRemove } from "@mdi/js";
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
const voteCounts = ref<Record<string, number>>({});
const voteCountValues = ref<number[]>([]);
const revealed = ref(false);

watch(
  () => props.issue,
  () => fetchVotes()
);

function fetchVotes() {
  getVotes(props.issue.id)
    .then((content: Vote[]) => {
      votes.value = content;
      userVote.value = content.find((v) => v.user.sub === userSub);
      countVotes();
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
        countVotes();
      })
      .catch((error) => snackbarStore.showMessage(error));
  } else {
    deleteVote(props.issue.id, userVote.value.id)
      .then(() => {
        userVote.value = undefined;
        revealed.value = false;
        fetchVotes();
      })
      .catch((error) => snackbarStore.showMessage(error));
  }
}

function countVotes() {
  voteCounts.value = votes.value.reduce(
    (acc, vote) => {
      acc[vote.voting] = (acc[vote.voting] || 0) + 1;
      return acc;
    },
    {} as Record<string, number>
  );
  voteCountValues.value = new Array(votingOptions.length).fill(0);
  Object.entries(voteCounts.value).forEach(([voting, count]) => {
    voteCountValues.value[votingOptions.indexOf(parseInt(voting))] = count;
  });
}
</script>
