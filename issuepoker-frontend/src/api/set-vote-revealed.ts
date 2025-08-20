import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setVoteRevealed(
  issue: IssueDetails,
  revealed: boolean
): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issue.owner}/${issue.repository}/${issue.number}/votes/revealed`,
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
