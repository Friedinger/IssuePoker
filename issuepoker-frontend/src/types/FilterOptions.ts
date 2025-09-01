export interface FilterOptions {
  owners: string[];
  repositories: string[];
  voted: boolean | null;
  resulted: boolean | null;
}

export function filtersToParams(filterOptions: FilterOptions): string {
  const params: string[] = [];
  params.push(`owners=${filterOptions.owners.join(",")}`);
  params.push(`repositories=${filterOptions.repositories.join(",")}`);
  params.push(`voted=${filterOptions.voted ?? ""}`);
  params.push(`resulted=${filterOptions.resulted ?? ""}`);
  return params.join("&");
}
