import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setVotesRevealed(
  issueId: string,
  revealed: boolean
): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes/revealed`,
    postConfig({
      revealed: revealed,
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
