import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getIssueList(
  pageNumber: number,
  pageSize: number
): Promise<Page<IssueSummary>> {
  return fetch(
    `api/backend-service/issues?pageNumber=${pageNumber}&pageSize=${pageSize}`,
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
