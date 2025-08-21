import type { IssueRemote } from "@/types/IssueRemote.ts";

import { defaultResponseHandler } from "@/api/fetch-utils";

export function getIssueRemote(
  owner: string,
  repository: string,
  number: number
): Promise<IssueRemote> {
  return fetch(
    `https://api.github.com/repos/${owner}/${repository}/issues/${number}`,
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

export function getConfig() {
  return {
    method: "GET",
    headers: {
      Accept: "application/vnd.github.v3+json",
    },
  };
}
