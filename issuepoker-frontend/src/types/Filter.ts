import type { LocationQuery, LocationQueryRaw } from "vue-router";

export interface Filter {
  search: string | undefined;
  owners: string[];
  repositories: string[];
  voted: boolean | undefined;
  resulted: boolean | undefined;
}

export function filterToParams(filter: Filter): string {
  const params: string[] = [];
  params.push(`search=${filter.search ?? ""}`);
  params.push(`owners=${filter.owners.join(",")}`);
  params.push(`repositories=${filter.repositories.join(",")}`);
  params.push(`voted=${filter.voted ?? ""}`);
  params.push(`resulted=${filter.resulted ?? ""}`);
  return params.join("&");
}

export function filterToQuery(filter: Filter): LocationQueryRaw {
  return {
    search: filter.search ?? undefined,
    owners: filter.owners.length > 0 ? filter.owners.join(",") : undefined,
    repositories:
      filter.repositories.length > 0
        ? filter.repositories.join(",")
        : undefined,
    voted: filter.voted !== null ? String(filter.voted) : undefined,
    resulted: filter.resulted !== null ? String(filter.resulted) : undefined,
  };
}

export function filterFromQuery(query: LocationQuery): Filter {
  return {
    search: (query.search as string) || undefined,
    owners: query.owners ? (query.owners as string).split(",") : [],
    repositories: query.repositories
      ? (query.repositories as string).split(",")
      : [],
    voted: parseBooleanQueryParam(query.voted),
    resulted: parseBooleanQueryParam(query.resulted),
  };
}

function parseBooleanQueryParam(param: unknown): boolean | undefined {
  if (param === "true") return true;
  if (param === "false") return false;
  return undefined;
}
