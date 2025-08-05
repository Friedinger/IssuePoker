import type Vote from "@/types/Vote.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getVotes(issueId: number): Promise<Vote[]> {
  return fetch(`api/backend-service/issues/${issueId}/votes`, getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
