import type Vote from "@/types/Vote.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils.ts";

export function createVote(
  issueId: string,
  userSub: string,
  voting: number
): Promise<Vote> {
  return fetch(
    `api/backend-service/issues/${issueId}/votes`,
    postConfig({
      userSub: userSub,
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
