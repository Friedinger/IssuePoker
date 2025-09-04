import type { RouteParamsGeneric } from "vue-router";

function parseRouteParam(param: string | string[] | undefined): string {
  return Array.isArray(param) ? (param[0] ?? "") : (param ?? "");
}

export function parseRouteParamsToIssueKey(params: RouteParamsGeneric): {
  owner: string;
  repository: string;
  number: number;
} {
  const { owner, repository, number } = params;
  return {
    owner: parseRouteParam(owner),
    repository: parseRouteParam(repository),
    number: parseInt(parseRouteParam(number)),
  };
}
