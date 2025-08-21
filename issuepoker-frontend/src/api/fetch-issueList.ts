import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort.js";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getIssueList(
  page: number,
  size: number,
  sortBy: SortItem[],
  search: string
): Promise<Page<IssueSummary>> {
  return fetch(
    `api/backend-service/issues?page=${page}&size=${size}&sort=${toString(sortBy)}&search=${search}`,
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

function toString(sortBy: SortItem[]): string {
  return sortBy
    .map((item) => {
      const orderString =
        typeof item.order === "boolean"
          ? item.order
            ? "asc"
            : "desc"
          : (item.order ?? "asc");
      return `${item.key},${orderString}`;
    })
    .join(";");
}
