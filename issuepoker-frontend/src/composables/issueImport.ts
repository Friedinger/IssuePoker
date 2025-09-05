import type IssueDetails from "@/types/IssueDetails.ts";
import type { IssueRemote } from "@/types/IssueRemote.ts";

import { ref } from "vue";
import { useRoute } from "vue-router";

import { getIssueRemote } from "@/api/issue/get-issue-remote.ts";
import { ROUTES_ISSUE_EDIT, STATUS_INDICATORS } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useIssueImportStore } from "@/stores/issueImport.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const issueUrlRegex =
  /^https:\/\/github\.com\/([\w-]+)\/([\w-]+)\/issues\/(\d+)$/;

export function useIssueImport() {
  const url = ref("");
  const valid = ref(false);
  const snackbarStore = useSnackbarStore();
  const issueImportStore = useIssueImportStore();
  const route = useRoute();

  function importIssue(): IssueDetails | undefined {
    const match = url.value.match(issueUrlRegex);
    if (!match) return;
    const [, owner = "", repository = "", number = ""] = match;
    getIssueRemote(owner, repository, parseInt(number))
      .then((content: IssueRemote) => {
        if (content.pull_request) {
          throw new Error("Pull Requests are not supported.");
        }
        const imported: IssueDetails = {
          owner,
          repository,
          number: content.number,
          title: content.title,
          description: content.body || "",
        };
        issueImportStore.setIssueImport(imported);
        if (route.name !== ROUTES_ISSUE_EDIT) {
          router
            .push({
              name: ROUTES_ISSUE_EDIT,
              params: { owner, repository, number, action: "new" },
            })
            .then();
        }
        snackbarStore.showMessage({
          message: `${owner}/${repository}#${number} wurde erfolgreich importiert.`,
          level: STATUS_INDICATORS.SUCCESS,
        });
      })
      .catch((e) => {
        if (e.message === "Pull Requests are not supported.") {
          snackbarStore.showMessage({
            message: `${owner}/${repository}#${number} ist ein Pull Request und wird nicht unterstützt.`,
            level: STATUS_INDICATORS.WARNING,
          });
        } else {
          snackbarStore.showMessage({
            message: `Remote Issue ${owner}/${repository}#${number} wurde nicht gefunden.`,
            level: STATUS_INDICATORS.WARNING,
          });
        }
      });
  }

  function validateUrl(value: string) {
    if (value.trim().length < 1) return "Bitte eine URL angeben.";
    if (!issueUrlRegex.test(value)) {
      return "Bitte eine gültige GitHub Issue URL angeben.";
    }
    return true;
  }

  return { url, valid, importIssue, validateUrl };
}
