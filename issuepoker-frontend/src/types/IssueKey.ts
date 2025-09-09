export default interface IssueKey {
  owner: string;
  repository: string;
  number: number;
}

export function issueKeyToString(issueKey: IssueKey) {
  return `${issueKey.owner}/${issueKey.repository}#${issueKey.number}`;
}
