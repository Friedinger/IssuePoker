import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueKey from "@/types/IssueKey.ts";
import type { Ref } from "vue";

import { readonly, ref, toValue, watchEffect } from "vue";

import { getIssue } from "@/api/issue/get-issue.ts";
import {
  ROUTES_HOME,
  ROUTES_ISSUE_EDIT,
  STATUS_INDICATORS,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useIssueImportStore } from "@/stores/issueImport.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { issueKeyToString } from "@/types/IssueKey.ts";
import { isAdmin } from "@/util/userUtils.ts";

export function useIssueDetails(
  issueKey: IssueKey | Ref<IssueKey> | (() => IssueKey)
) {
  const snackbarStore = useSnackbarStore();
  const issueImportStore = useIssueImportStore();
  const issue = ref<IssueDetails>();
  const key = ref<IssueKey>(toValue(issueKey));

  watchEffect(() => {
    key.value = toValue(issueKey);
    fetchIssue();
  });

  function fetchIssue() {
    getIssue(key.value.owner, key.value.repository, key.value.number)
      .then((content: IssueDetails) => (issue.value = content))
      .catch(() => {
        if (isAdmin()) {
          handleNotFoundAdmin();
        } else {
          handleNotFoundUser();
        }
      });
  }

  function handleNotFoundAdmin() {
    issueImportStore.setIssueImport({
      ...key.value,
      title: "",
      description: "",
      labels: {},
    });
    snackbarStore.showMessage({
      message: `${issueKeyToString(key.value)} wurde nicht gefunden. Ein neues Issue kann jetzt erstellt werden.`,
      level: STATUS_INDICATORS.INFO,
    });
    router
      .push({
        name: ROUTES_ISSUE_EDIT,
        params: {
          ...key.value,
          action: "new",
        },
      })
      .then();
  }

  function handleNotFoundUser() {
    snackbarStore.showMessage({
      message: `${issueKeyToString(key.value)} wurde nicht gefunden.`,
      level: STATUS_INDICATORS.WARNING,
    });
    router.push({ name: ROUTES_HOME }).then();
  }

  return { issue: readonly(issue), fetchIssue };
}
