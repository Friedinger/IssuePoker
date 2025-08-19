import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, deleteConfig } from "@/api/fetch-utils.ts";

export function deleteVote(issue: IssueDetails): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issue.owner}/${issue.repository}/${issue.id}/votes`,
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
