import type Votes from "@/types/Votes.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils.ts";

export function createVote(issueId: string, voting: number): Promise<Votes> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes`,
    postConfig({
      voting: voting,
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
