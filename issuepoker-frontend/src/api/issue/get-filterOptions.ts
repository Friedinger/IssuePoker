import type { Filter } from "@/types/Filter.ts";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils.ts";

export function getFilterOptions(): Promise<Filter> {
  return fetch("api/backend-service/issues/filterOptions", getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
