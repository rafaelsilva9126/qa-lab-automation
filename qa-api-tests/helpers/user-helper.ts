import { expect, APIRequestContext } from "@playwright/test";
import { ApiClient } from "../clients/api-client";

export async function createUser(request: APIRequestContext) {
  const api = new ApiClient(request);

  const payload = {
    email: `user_${Date.now()}@mail.com`,
    password: "123456",
  };

  const response = await api.createUser(payload);
  console.log("createUser status:", response.status());
  console.log("createUser body:", await response.text());
  expect(response.status()).toBe(200);

  return response.json();
}
