<template>
  <v-row>
    <v-col><h2>Pokern</h2></v-col>
  </v-row>
  <v-row class="mt-0">
    <v-col v-if="!revealed">
      <p v-if="!votes.userVoting">
        Die Abstimmung läuft. Klicke auf einen Wert um dafür zu stimmen.
      </p>
      <p v-else>
        Die Abstimmung läuft. Klicke auf einen anderen Wert um die Stimme zu
        ändern oder auf den Aktuellen um die Stimme zurückzunehmen.
      </p>
    </v-col>
    <v-col v-else-if="isAdmin()">
      <p v-if="!votes.voteResult">
        Die Abstimmung ist beendet. Klicke auf einen Wert um diesen als Ergebnis
        zu setzen.
      </p>
      <p v-else>
        Klicke auf einen anderen Wert um das Ergebnis zu ändern oder auf das
        Aktuelle um es zurückzunehmen.
      </p>
    </v-col>
    <v-col v-else>
      <p v-if="!votes.voteResult">
        Die Abstimmung ist beendet. Bitte warte bis ein Admin das Ergebnis
        setzt.
      </p>
      <p v-else>Die Abstimmung ist beendet. Ein Ergebnis wurde festgelegt.</p>
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
            :class="
              (votes.userVoting === votingOption ? 'userVoting' : '') +
              ' ' +
              (votes.voteResult === votingOption ? 'voteResult' : '')
            "
            :disabled="revealed && !isAdmin()"
            stacked
            @click="vote(votingOption)"
          >
            <v-row
              ><span class="votingOption">{{ votingOption }}</span></v-row
            >
            <v-row>
              <v-icon v-if="votes.userVoting === votingOption">
                {{ mdiAccount }}
              </v-icon>
              <v-icon v-if="votes.voteResult === votingOption">
                {{ mdiTrophy }}
              </v-icon>
            </v-row>
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
            :disabled="isDefined(votes.voteResult)"
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

import {
  mdiAccount,
  mdiDelete,
  mdiEye,
  mdiEyeRemove,
  mdiTrophy,
} from "@mdi/js";
import { isDefined } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted, ref, watch } from "vue";

import { createVote } from "@/api/vote/create-vote.ts";
import { deleteAllVotes } from "@/api/vote/delete-vote-all.ts";
import { deleteVote } from "@/api/vote/delete-vote.ts";
import { fetchVotes } from "@/api/vote/subscribe-votes.ts";
import { setVoteResult } from "@/api/vote/set-voteResult.ts";
import { setVoteRevealed } from "@/api/vote/set-voteRevealed.ts";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { STATUS_INDICATORS } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useVotingOptionsStore } from "@/stores/votingOptions.ts";
import { isAdmin, isLoggedIn } from "@/util/userUtils.ts";

const { getVotingOptions } = storeToRefs(useVotingOptionsStore());
const notLoggedInMessage: SnackbarState = {
  message: "Bitte anmelden um die Poker Funktion zu nutzen.",
  level: STATUS_INDICATORS.ERROR,
  show: true,
};

const snackbarStore = useSnackbarStore();
const props = defineProps(["issue"]);
const votes = ref<Votes>({ voteCount: 0 });
const voteCounts = ref<Record<string, number>>({});
const voteCountValues = ref<number[]>([]);
const revealed = ref(false);
const deleteDialog = ref(false);

let eventSource: EventSource | null = null;

onMounted(() => {
  if (props.issue) {
    fetchVotes();
  }
});

watch(
  () => props.issue,
  () => fetchVotes()
);

onUnmounted(() => {
  if (eventSource) eventSource.close();
});

function fetchVotes() {
  if (!isLoggedIn()) {
    snackbarStore.showMessage(notLoggedInMessage);
    return;
  }
  if (eventSource) eventSource.close();
  eventSource = fetchVotes(
    props.issue,
    (content) => {
      votes.value = content;
      revealed.value = isDefined(content.allVotings);
      countVotes();
    },
    () => setTimeout(fetchVotes, 1000)
  );
}

function vote(voting: number) {
  if (isAdmin() && revealed.value) {
    setVoteResult(
      props.issue,
      voting === votes.value.voteResult ? undefined : voting
    ).catch((error) => snackbarStore.showMessage(error));
  } else if (votes.value?.userVoting != voting) {
    createVote(props.issue, voting).catch((error) =>
      snackbarStore.showMessage(error)
    );
  } else {
    deleteVote(props.issue).catch((error) => snackbarStore.showMessage(error));
  }
}

function toggleRevealed() {
  setVoteRevealed(props.issue, !revealed.value).catch((error) =>
    snackbarStore.showMessage(error)
  );
}

function resetVotes() {
  deleteDialog.value = false;
  deleteAllVotes(props.issue).catch((error) =>
    snackbarStore.showMessage(error)
  );
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
</script>

<style scoped>
.votingOption {
  font-size: 1.5rem;
}

/*noinspection CssUnusedSymbol*/
.userVoting {
  background: #5e73c9;
}

/*noinspection CssUnusedSymbol*/
.voteResult {
  background: #ffcd00;
}
</style>
