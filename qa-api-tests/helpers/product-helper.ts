import { expect, APIRequestContext } from '@playwright/test';
import { ApiClient } from '../clients/api-client';

export async function createProduct(
  request: APIRequestContext,
  payload: { name: string; price: number; userId: number }
) {
  const api = new ApiClient(request);

  const response = await api.createProduct(payload);
  expect(response.status()).toBe(200);

  return response.json();
}