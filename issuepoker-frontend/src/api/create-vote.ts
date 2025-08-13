import { defaultResponseHandler, postConfig } from "@/api/fetch-utils.ts";

export function createVote(issueId: string, voting: number): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes`,
    postConfig({
      voting: voting,
    })
  )
    .then((response) => {
      defaultResponseHandler(response);
      return;
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
