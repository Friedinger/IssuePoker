import type IssueDetails from "@/types/IssueDetails.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setIssueRevealed(
  issueId: string,
  revealed: boolean
): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues/${issueId}/revealed`,
    postConfig({
      revealed: revealed,
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
