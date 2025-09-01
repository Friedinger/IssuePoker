export interface Filter {
  search: string | undefined;
  owners: string[];
  repositories: string[];
  voted: boolean | null;
  resulted: boolean | null;
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
