import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getIssueList(
  page: number,
  size: number,
  sortBy: SortItem[]
): Promise<Page<IssueSummary>> {
  return fetch(
    `api/backend-service/issues?page=${page}&size=${size}`,
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
