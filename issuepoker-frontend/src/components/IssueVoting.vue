<template>
  <v-row>
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
  <v-row>
    <v-col cols="auto">
      <p>Anzahl Stimmen: {{ votes.voteCount }}</p>
      <p>Ergebnis: {{ votes.voteResult || "-" }}</p>
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
            density="comfortable"
            rounded="rounded"
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
            density="comfortable"
            rounded="rounded"
            v-bind="props"
            @click="resetDialog = true"
          />
        </template>
      </v-tooltip>
      <yes-no-dialog
        v-model="resetDialog"
        dialogtext="Sollen wirklich alle Stimmen dieses Issues zurückgesetzt werden?"
        dialogtitle="Stimmen zurücksetzen"
        @no="resetDialog = false"
        @yes="resetVotes()"
      />
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
            :class="
              (votes.userVoting === votingOption ? 'userVoting' : '') +
              ' ' +
              (votes.voteResult === votingOption ? 'voteResult' : '')
            "
            :disabled="revealed && !isAdmin()"
            stacked
            @click="setVote(votingOption)"
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
            v-model="graphValues"
            :gradient="['#FFCD00', '#334799']"
            auto-draw
            smooth="20"
            stroke-linecap="round"
          />
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang="ts" setup>
import type IssueDetails from "@/types/IssueDetails.ts";

import {
  mdiAccount,
  mdiDelete,
  mdiEye,
  mdiEyeRemove,
  mdiTrophy,
} from "@mdi/js";
import { isDefined } from "@vueuse/core";
import { computed, onMounted, ref, watch } from "vue";

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { useIssueVoting } from "@/composables/issueVoting.ts";
import { isAdmin } from "@/util/userUtils.ts";

const props = defineProps<{
  issue: IssueDetails;
}>();
const issue = ref<IssueDetails>(props.issue);
const {
  votes,
  revealed,
  voteCounts,
  voteCountValues,
  votingOptions,
  resetDialog,
  fetchVotes,
  setVote,
  toggleRevealed,
  resetVotes,
} = useIssueVoting(issue);
const graphValues = computed(() => [...voteCountValues.value]);

onMounted(() => {
  parseProps(props.issue);
});

watch(
  () => props.issue,
  (value) => parseProps(value)
);

function parseProps(issueProp: IssueDetails) {
  if (issueProp) {
    issue.value = issueProp;
    fetchVotes();
  }
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
