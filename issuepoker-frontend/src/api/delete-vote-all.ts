import { defaultResponseHandler, deleteConfig } from "@/api/fetch-utils.ts";

export function deleteAllVotes(issueId: string): Promise<void> {
  return fetch(`api/backend-service/issues/${issueId}/votes`, deleteConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return;
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
