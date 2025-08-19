import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueRequest from "@/types/IssueRequest.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getIssue(request: IssueRequest): Promise<IssueDetails> {
  return fetch(
    `api/backend-service/issues/${request.owner}/${request.repository}/${request.id}`,
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
