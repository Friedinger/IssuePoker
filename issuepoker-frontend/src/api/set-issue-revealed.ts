import { defaultResponseHandler, postConfig } from "@/api/fetch-utils";

export function setIssueRevealed(
  issueId: string,
  revealed: boolean
): Promise<void> {
  return fetch(
    `api/backend-service/issues/${issueId}/revealed`,
    postConfig({
      revealed: revealed,
    })
  )
    .then((response) => {
      defaultResponseHandler(response);
      return;
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
