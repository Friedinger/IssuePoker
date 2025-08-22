import type { FilterOptions } from "@/types/FilterOptions";

import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export function getFilterOptions(): Promise<FilterOptions> {
  return fetch("api/backend-service/issues/filterOptions", getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
