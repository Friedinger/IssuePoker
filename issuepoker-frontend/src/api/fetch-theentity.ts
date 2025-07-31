import { defaultResponseHandler, getConfig } from "@/api/fetch-utils";

export interface TheEntity {
  id: string;
  textAttribute: string;
}

export function getEntity(): Promise<TheEntity> {
  return fetch(
    "api/backend-service/theEntity/123e4567-e89b-12d3-a456-426614174000",
    getConfig()
  )
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
