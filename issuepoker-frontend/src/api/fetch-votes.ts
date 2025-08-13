import type Votes from "@/types/Votes";

export function subscribeVotes(
  issueId: number,
  onMessage: (votes: Votes) => void,
  onError?: () => void
): EventSource {
  const eventSource = new EventSource(
    `api/backend-service/issues/${issueId}/votes`
  );
  eventSource.addEventListener("votes", (event) => {
    const content = JSON.parse((event as MessageEvent).data);
    onMessage(content);
  });
  eventSource.onerror = () => {
    eventSource.close();
    if (onError) onError();
  };
  return eventSource;
}
