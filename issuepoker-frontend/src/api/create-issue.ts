import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function createIssue(
  title: string,
  description: string
): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues`,
    postConfig({
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
