import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setVoteResult(
  issueId: string,
  voteResult?: number
): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes/result`,
    postConfig({
      voteResult: voteResult,
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
