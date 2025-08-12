<template>
  <v-row>
    <v-col><h2>Pokern</h2></v-col>
  </v-row>
  <v-row class="mt-0">
    <v-col>
      <p v-if="!votes.userVoting">Klicke auf einen Wert um dafür zu stimmen.</p>
      <p v-else>
        Klicke auf einen anderen Wert um die Stimme zu ändern oder auf den
        Aktuellen um die Stimme zurückzunehmen.
      </p>
    </v-col>
  </v-row>
  <v-row class="flex-nowrap overflow-x-auto">
    <v-col cols="auto">
      <v-row class="flex-nowrap">
        <v-col
          v-for="votingOption in getVotingOptions"
          :key="votingOption"
          cols="auto"
        >
          <v-btn
            :class="votes.userVoting === votingOption ? 'userVote' : ''"
            :disabled="revealed"
            @click="vote(votingOption)"
          >
            {{ votingOption }}
          </v-btn>
          <p
            v-if="revealed"
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
            :icon="!revealed ? mdiEye : mdiEyeRemove"
            v-bind="props"
            @click="toggleRevealed()"
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
        dialogtext="Sollen wirklich alle Stimmen dieses Issues zurückgesetzt werden?"
        dialogtitle="Stimmen zurücksetzen"
        @no="deleteDialog = false"
        @yes="resetVotes()"
      />
    </v-col>
    <v-col cols="auto">
      <p>Anzahl Stimmen: {{ votes.voteCount }}</p>
      <p>Ergebnis: {{ votes.voteResult || "-" }}</p>
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import type { SnackbarState } from "@/stores/snackbar.ts";
import type Votes from "@/types/Votes.ts";

import { mdiDelete, mdiEye, mdiEyeRemove } from "@mdi/js";
import { isDefined } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { createVote } from "@/api/create-vote.ts";
import { deleteAllVotes } from "@/api/delete-vote-all.ts";
import { deleteVote } from "@/api/delete-vote.ts";
import { getVotes } from "@/api/fetch-votes.ts";
import { setVotesRevealed } from "@/api/set-votes-revealed.ts";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { ROLE_ADMIN, STATUS_INDICATORS } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";
import { useVotingOptionsStore } from "@/stores/votingOptions.ts";

const { getVotingOptions } = storeToRefs(useVotingOptionsStore());
const notLoggedInMessage: SnackbarState = {
  message: "Bitte anmelden um die Poker Funktion zu nutzen.",
  level: STATUS_INDICATORS.ERROR,
  show: true,
};

const snackbarStore = useSnackbarStore();
const { getUser } = storeToRefs(useUserStore());
const props = defineProps(["issue"]);
const votes = ref<Votes>({ voteCount: 0 });
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
  getVotes(props.issue.id)
    .then((content: Votes) => {
      votes.value = content;
      revealed.value = isDefined(content.allVotings);
      countVotes();
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}

function vote(voting: number) {
  if (votes.value?.userVoting != voting) {
    createVote(props.issue.id, voting)
      .then((content: Votes) => {
        votes.value = content;
        countVotes();
      })
      .catch((error) => snackbarStore.showMessage(error));
  } else {
    deleteVote(props.issue.id)
      .then(() => fetchVotes())
      .catch((error) => snackbarStore.showMessage(error));
  }
}

function toggleRevealed() {
  setVotesRevealed(props.issue.id, !revealed.value)
    .then(() => fetchVotes())
    .catch((error) => snackbarStore.showMessage(error));
}

function resetVotes() {
  deleteDialog.value = false;
  deleteAllVotes(props.issue.id)
    .then(() => fetchVotes())
    .catch((error) => snackbarStore.showMessage(error));
}

function countVotes() {
  if (!votes.value.allVotings) return;
  voteCounts.value = votes.value.allVotings.reduce(
    (acc, voting) => {
      acc[voting] = (acc[voting] || 0) + 1;
      return acc;
    },
    {} as Record<string, number>
  );
  voteCountValues.value = new Array(getVotingOptions.value.length).fill(0);
  Object.entries(voteCounts.value).forEach(([voting, count]) => {
    voteCountValues.value[getVotingOptions.value.indexOf(parseInt(voting))] =
      count;
  });
}

function isAdmin(): boolean {
  if (!getUser.value) {
    return false;
  }
  return getUser.value.authorities.includes(ROLE_ADMIN);
}
</script>

<style scoped>
/*noinspection CssUnusedSymbol*/
.userVote {
  background: #ffcd00;
}
</style>
