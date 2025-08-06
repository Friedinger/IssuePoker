import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getIssue(id: number): Promise<IssueDetails> {
  return fetch(`api/backend-service/issues/${id}`, getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
