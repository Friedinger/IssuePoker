import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, deleteConfig } from "@/api/fetch-utils.ts";

export function unsubscribeVotes(issue: IssueDetails): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issue.owner}/${issue.repository}/${issue.number}/votes/unsubscribe`,
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
