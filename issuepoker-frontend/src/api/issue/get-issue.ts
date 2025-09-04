import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils.ts";

export function getIssue(
  owner: string,
  repository: string,
  number: number
): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues/${owner}/${repository}/${number}`,
    getConfig()
  )
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
