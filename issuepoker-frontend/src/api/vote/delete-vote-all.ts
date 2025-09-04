import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, deleteConfig } from "@/api/fetch-utils.ts";

export function deleteAllVotes(issue: IssueDetails): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issue.owner}/${issue.repository}/${issue.number}/votes/all`,
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
