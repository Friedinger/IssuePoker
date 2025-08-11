import type { VotingOptions } from "@/stores/votingOptions.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getVotingOptions(): Promise<VotingOptions> {
  return fetch(`api/backend-service/issues/votingOptions`, getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
