export default interface IssueSummary {
  owner: string;
  repository: string;
  number: number;
  title: string;
  voteCount: number;
  voteResult?: number;
}
