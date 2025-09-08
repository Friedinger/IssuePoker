import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, patchConfig } from "@/api/fetch-utils.ts";

export function updateIssue(
  owner: string,
  repository: string,
  number: number,
  title: string,
  description: string,
  labels: Record<string, string>
): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues/${owner}/${repository}/${number}`,
    patchConfig({
      title: title,
      description: description,
      labels: labels,
    })
  )
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
