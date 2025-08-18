export default interface IssueSummary {
  id: number;
  owner: string;
  repository: string;
  title: string;
  voteCount: number;
  voteResult?: number;
}
