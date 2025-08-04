import type Issue from "@/types/Issue.ts";

import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function createIssue(
  title: string,
  description: string
): Promise<Issue> {
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
