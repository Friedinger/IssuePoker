import { defaultResponseHandler, deleteConfig } from "@/api/fetch-utils.ts";

export function deleteVote(issueId: string, voteId: string): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes/${voteId}`,
    deleteConfig()
  )
    .then((response) => {
      defaultResponseHandler(response);
      return;
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
