import { defaultResponseHandler, getConfig } from "@/api/fetch-utils.ts";
import HealthState from "@/types/HealthState.ts";

export function checkGatewayHealth(): Promise<HealthState> {
  return fetch("actuator/health", getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}

export function checkBackendHealth(): Promise<HealthState> {
  return fetch("api/backend-service/actuator/health", getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
