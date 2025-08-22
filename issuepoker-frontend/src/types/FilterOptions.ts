export interface FilterOptions {
  owners: string[];
  repositories: string[];
}

export function filtersToParams(filterOptions: FilterOptions): string {
  const params: string[] = [];
  params.push(`owners=${filterOptions.owners.join(",")}`);
  params.push(`repositories=${filterOptions.repositories.join(",")}`);
  return params.join("&");
}
