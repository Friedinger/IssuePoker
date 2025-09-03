import type IssueDetails from "@/types/IssueDetails.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

type IssueImportState = IssueDetails | null;

export const useIssueImportStore = defineStore("issueImport", () => {
  const issueImport = ref<IssueImportState>(null);

  const getIssueImport = computed((): IssueImportState => {
    return issueImport.value;
  });

  function setIssueImport(payload: IssueImportState): void {
    issueImport.value = payload;
  }

  return { getIssueImport, setIssueImport };
});
