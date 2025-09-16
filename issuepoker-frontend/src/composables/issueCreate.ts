import type { ComposableParam } from "@/types/ComposableParam.ts";
import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueKey from "@/types/IssueKey.ts";

import { readonly, ref, toValue, watchEffect } from "vue";

import { getIssue } from "@/api/issue/get-issue.ts";
import { ROUTES_ISSUE_EDIT } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useIssueImportStore } from "@/stores/issueImport.ts";

export function useIssueCreate(
  issueKey: ComposableParam<IssueKey | undefined>
) {
  const issueImportStore = useIssueImportStore();
  const issue = ref<IssueDetails>();
  const originalIssue = ref<IssueDetails>();
  const key = ref<IssueKey>();

  watchEffect(() => {
    const keyOptional = toValue(issueKey);
    if (
      keyOptional &&
      keyOptional.owner !== "" &&
      keyOptional.repository !== "" &&
      !isNaN(keyOptional.number)
    ) {
      key.value = toValue(issueKey);
      fetchIssue();
    }
  });

  function fetchIssue() {
    const imported = importIssue(issueImportStore.getIssueImport);
    if (!key.value || imported) return;
    getIssue(key.value.owner, key.value.repository, key.value.number)
      .then((content: IssueDetails) => issueExists(content))
      .catch(() => issueExistsNot());
  }

  function issueExists(content: IssueDetails) {
    router
      .push({
        name: ROUTES_ISSUE_EDIT,
        params: { ...key.value, action: "edit" },
      })
      .then();
    issue.value = content;
    originalIssue.value = issue.value;
  }

  function issueExistsNot() {
    router
      .push({
        name: ROUTES_ISSUE_EDIT,
        params: { ...key.value, action: "new" },
      })
      .then();
    if (!key.value) return;
    issue.value = getDefaultIssue(key.value);
    originalIssue.value = issue.value;
  }

  function getDefaultIssue(issueKey: IssueKey): IssueDetails {
    return {
      ...issueKey,
      title: "",
      description: "",
      labels: {},
    };
  }

  function importIssue(imported: IssueDetails | null): boolean {
    if (imported) {
      issue.value = imported;
      key.value = {
        owner: imported.owner,
        repository: imported.repository,
        number: imported.number,
      };
      issueImportStore.setIssueImport(null);
      return true;
    }
    return false;
  }

  return {
    issue: readonly(issue),
    originalIssue: readonly(originalIssue),
    fetchIssue,
    importIssue,
  };
}
