import type { ComposableParam } from "@/types/ComposableParam.ts";
import type IssueDetails from "@/types/IssueDetails.ts";
import type { RouteLocationRaw } from "vue-router";

import { ref, toValue } from "vue";

import { createIssue } from "@/api/issue/create-issue.ts";
import { updateIssue } from "@/api/issue/update-issue.ts";
import {
  ROUTES_HOME,
  ROUTES_ISSUE_DETAIL,
  STATUS_INDICATORS,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

export function useIssueCreateForm(
  action: ComposableParam<"new" | "edit">,
  issue: ComposableParam<IssueDetails | undefined>,
  originalIssue: ComposableParam<IssueDetails | undefined>
) {
  const snackbarStore = useSnackbarStore();
  const owner = ref("");
  const repository = ref("");
  const number = ref(NaN);
  const title = ref("");
  const labels = ref<Record<string, string>>({});
  const description = ref("");
  const valid = ref();
  const saved = ref<boolean>(false);

  function save() {
    if (!valid.value) return;
    const request = toValue(action) === "edit" ? updateIssue : createIssue;
    request(
      owner.value,
      repository.value,
      number.value,
      title.value,
      description.value,
      labels.value
    )
      .then((content: IssueDetails) => {
        saved.value = true;
        const { owner, repository, number } = content;
        router
          .push({
            name: ROUTES_ISSUE_DETAIL,
            params: { owner, repository, number },
          })
          .then();
        snackbarStore.showMessage({
          message: `${owner}/${repository}#${number} wurde erfolgreich ${action === "edit" ? "bearbeitet" : "angelegt"}.`,
          level: STATUS_INDICATORS.SUCCESS,
        });
      })
      .catch((error) => snackbarStore.showMessage(error));
  }

  function validateOwner(value: string) {
    if (value.trim().length < 1) return "Bitte einen Besitzer angeben.";
    if (value.length > 255)
      return "Besitzer darf nicht länger als 255 Zeichen sein.";
    return true;
  }

  function validateRepository(value: string) {
    if (value.trim().length < 1) return "Bitte eine Repository angeben.";
    if (value.length > 255)
      return "Repository darf nicht länger als 255 Zeichen sein.";
    return true;
  }

  function validateNumber(value: number | null) {
    if (value === null) return "Bitte eine Nummer angeben.";
    if (value < 1) return "Nummer muss positiv sein.";
    return true;
  }

  function validateTitle(value: string) {
    if (value.trim().length < 1) return "Bitte einen Titel angeben.";
    if (value.length > 255)
      return "Titel darf nicht länger als 255 Zeichen sein.";
    return true;
  }

  function validateDescription(value: string) {
    if (value.length > 65_535)
      return "Beschreibung darf nicht länger als 65535 Zeichen sein.";
    return true;
  }

  function cancel(): RouteLocationRaw {
    const issueValue = toValue(issue);
    if (action === "edit" && issueValue) {
      const { owner, repository, number } = issueValue;
      return {
        name: ROUTES_ISSUE_DETAIL,
        params: { owner, repository, number },
      };
    }
    return { name: ROUTES_HOME };
  }

  function isDirty(): boolean {
    if (saved.value === true) return false;
    const currentValues = [
      owner.value,
      repository.value,
      number.value,
      title.value,
      labels.value,
      description.value,
    ];
    const original = toValue(originalIssue);
    const originalValues = [
      original?.owner ?? "",
      original?.repository ?? "",
      original?.number ?? null,
      original?.title ?? "",
      original?.labels ?? {},
      original?.description ?? "",
    ];
    return currentValues.some((value, index) => {
      return JSON.stringify(value) !== JSON.stringify(originalValues[index]);
    });
  }

  return {
    owner,
    repository,
    number,
    title,
    labels,
    description,
    valid,
    save,
    validateOwner,
    validateRepository,
    validateNumber,
    validateTitle,
    validateDescription,
    cancel,
    isDirty,
  };
}
