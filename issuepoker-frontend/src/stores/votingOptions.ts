import { defineStore } from "pinia";
import { computed, ref } from "vue";

export type VotingOptions = number[];

export const useVotingOptionsStore = defineStore("votingOptions", () => {
  const votingOptions = ref<VotingOptions>([]);

  const getVotingOptions = computed((): VotingOptions => {
    return votingOptions.value;
  });

  function setVotingOptions(payload: VotingOptions): void {
    votingOptions.value = payload;
  }

  return { getVotingOptions, setVotingOptions };
});
