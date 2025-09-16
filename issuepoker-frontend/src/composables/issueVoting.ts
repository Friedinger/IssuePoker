import type IssueDetails from "@/types/IssueDetails.ts";
import type Votes from "@/types/Votes.ts";
import type { Ref } from "vue";

import { isDefined } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { onUnmounted, readonly, ref, toValue, watchEffect } from "vue";

import { createVote } from "@/api/vote/create-vote.ts";
import { deleteAllVotes } from "@/api/vote/delete-vote-all.ts";
import { deleteVote } from "@/api/vote/delete-vote.ts";
import { setVoteResult } from "@/api/vote/set-voteResult.ts";
import { setVoteRevealed } from "@/api/vote/set-voteRevealed.ts";
import { subscribeVotes } from "@/api/vote/subscribe-votes.ts";
import { STATUS_INDICATORS } from "@/constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useVotingOptionsStore } from "@/stores/votingOptions.ts";
import { isAdmin, isLoggedIn } from "@/util/userUtils.ts";

export function useIssueVoting(
  issue: IssueDetails | Ref<IssueDetails> | (() => IssueDetails)
) {
  const snackbarStore = useSnackbarStore();
  const { getVotingOptions: votingOptions } = storeToRefs(
    useVotingOptionsStore()
  );

  const votes = ref<Votes>({ voteCount: 0 });
  const voteCounts = ref<Record<string, number>>({});
  const voteCountValues = ref<number[]>([]);
  const revealed = ref(false);
  const resetDialog = ref(false);

  const eventSource = ref<EventSource | null>(null);

  onUnmounted(() => {
    if (eventSource.value) eventSource.value.close();
  });

  watchEffect(() => {
    fetchVotes();
  });

  function fetchVotes() {
    if (!isLoggedIn()) {
      snackbarStore.showMessage({
        message: "Bitte anmelden um die Poker Funktion zu nutzen.",
        level: STATUS_INDICATORS.ERROR,
        show: true,
      });
      return;
    }
    if (eventSource.value) eventSource.value.close();
    eventSource.value = subscribeVotes(
      toValue(issue),
      (content) => {
        votes.value = content;
        revealed.value = isDefined(content.allVotings);
        countVotes();
      },
      () => setTimeout(fetchVotes, 1000)
    );
  }

  function setVote(voting: number) {
    if (isAdmin() && revealed.value) {
      setVoteResult(
        toValue(issue),
        voting === votes.value.voteResult ? undefined : voting
      ).catch((error) => snackbarStore.showMessage(error));
    } else if (votes.value?.userVoting != voting) {
      createVote(toValue(issue), voting).catch((error) =>
        snackbarStore.showMessage(error)
      );
    } else {
      deleteVote(toValue(issue)).catch((error) =>
        snackbarStore.showMessage(error)
      );
    }
  }

  function toggleRevealed() {
    setVoteRevealed(toValue(issue), !revealed.value).catch((error) =>
      snackbarStore.showMessage(error)
    );
  }

  function resetVotes() {
    resetDialog.value = false;
    deleteAllVotes(toValue(issue))
      .then(() => {
        snackbarStore.showMessage({
          message: "Stimmen wurden erfolgreich zurückgesetzt.",
          level: STATUS_INDICATORS.SUCCESS,
        });
      })
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
    voteCountValues.value = new Array(votingOptions.value.length).fill(0);
    Object.entries(voteCounts.value).forEach(([voting, count]) => {
      voteCountValues.value[votingOptions.value.indexOf(parseInt(voting))] =
        count;
    });
  }

  return {
    votes: readonly(votes),
    revealed: readonly(revealed),
    voteCounts: readonly(voteCounts),
    voteCountValues: readonly(voteCountValues),
    votingOptions: readonly(votingOptions),
    resetDialog,
    fetchVotes,
    setVote,
    toggleRevealed,
    resetVotes,
  };
}
