export default interface IssueDetails {
  owner: string;
  repository: string;
  number: number;
  title: string;
  description: string;
  labels: Record<string, string>;
}
