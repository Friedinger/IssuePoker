import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setVoteResult(
  issue: IssueDetails,
  voteResult?: number
): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issue.owner}/${issue.repository}/${issue.id}/votes/result`,
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
