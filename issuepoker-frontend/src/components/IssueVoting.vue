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
    <v-col
      v-if="isAdmin()"
      class="d-flex ga-2"
      cols="auto"
    >
      <v-tooltip
        location="top"
        text="Stimmen anzeigen"
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
      <v-tooltip
        location="top"
        text="Stimmen zurücksetzen"
      >
        <template v-slot:activator="{ props }">
          <v-btn
            :icon="mdiDelete"
            v-bind="props"
            @click="deleteDialog = true"
          />
        </template>
      </v-tooltip>
      <yes-no-dialog
        v-model="deleteDialog"
        dialogtext="Sollen wirklick alle Stimmen dieses Issues zurückgesetzt werden?"
        dialogtitle="Stimmen zurücksetzen"
        @no="deleteDialog = false"
        @yes="resetVotes()"
      />
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import type { SnackbarState } from "@/stores/snackbar.ts";
import type Vote from "@/types/Vote.ts";

import { mdiDelete, mdiEye, mdiEyeRemove } from "@mdi/js";
import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { createVote } from "@/api/create-vote.ts";
import { deleteAllVotes } from "@/api/delete-vote-all.ts";
import { deleteVote } from "@/api/delete-vote.ts";
import { getVotes } from "@/api/fetch-votes.ts";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { ROLE_ADMIN, STATUS_INDICATORS } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";

const votingOptions = [1, 2, 3, 5, 8, 13, 21];
const notLoggedInMessage: SnackbarState = {
  message: "Bitte anmelden um die Poker Funktion zu nutzen.",
  level: STATUS_INDICATORS.ERROR,
  show: true,
};

const snackbarStore = useSnackbarStore();
const { getUser } = storeToRefs(useUserStore());
const props = defineProps(["issue"]);
const votes = ref<Vote[]>([]);
const userVote = ref<Vote>();
const voteCounts = ref<Record<string, number>>({});
const voteCountValues = ref<number[]>([]);
const revealed = ref(false);
const deleteDialog = ref(false);

watch(
  () => props.issue,
  () => fetchVotes()
);

function fetchVotes() {
  if (!getUser.value) {
    snackbarStore.showMessage(notLoggedInMessage);
    return;
  }
  const username = getUser.value.preferred_username;
  getVotes(props.issue.id)
    .then((content: Vote[]) => {
      votes.value = content;
      userVote.value = content.find((v) => v.username === username);
      countVotes();
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function vote(voting: number) {
  if (!getUser.value) {
    snackbarStore.showMessage(notLoggedInMessage);
    return;
  }
  if (!userVote.value) {
    createVote(props.issue.id, getUser.value.sub, voting)
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

function resetVotes() {
  deleteDialog.value = false;
  deleteAllVotes(props.issue.id)
    .then(() => {
      userVote.value = undefined;
      revealed.value = false;
      fetchVotes();
    })
    .catch((error) => snackbarStore.showMessage(error));
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

function isAdmin(): boolean {
  if (!getUser.value) {
    return false;
  }
  return getUser.value.authorities.includes(ROLE_ADMIN);
}
</script>
