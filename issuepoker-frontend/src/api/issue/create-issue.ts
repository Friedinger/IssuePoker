import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils.ts";

export function createIssue(
  owner: string,
  repository: string,
  number: number,
  title: string,
  description: string
): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues`,
    postConfig({
      owner: owner,
      repository: repository,
      number: number,
      title: title,
      description: description,
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
