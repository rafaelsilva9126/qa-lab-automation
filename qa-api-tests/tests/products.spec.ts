import { test, expect } from '@playwright/test';
import { ApiClient } from '../clients/api-client';
import { createUser } from '../helpers/user-helper';

test.describe('Products API', () => {
  test('should create a product successfully', async ({ request }) => {
    const api = new ApiClient(request);
    const user = await createUser(request);

    const response = await api.createProduct({
      name: 'Keyboard',
      price: 100.0,
      userId: user.id,
    });

    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.id).toBeDefined();
    expect(body.name).toBe('Keyboard');
    expect(body.price).toBe(100);
    expect(body.userId).toBe(user.id);
  });

  test('should return error when user does not exist', async ({ request }) => {
    const api = new ApiClient(request);

    const response = await api.createProduct({
      name: 'Mouse',
      price: 50.0,
      userId: 999999,
    });

    expect(response.status()).toBe(400);

    const body = await response.text();
    expect(body).toContain('User not found');
  });

  test('should get all products', async ({ request }) => {
    const api = new ApiClient(request);

    const response = await api.getProducts();
    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(Array.isArray(body)).toBeTruthy();
  });

  test('should get product by id', async ({ request }) => {
    const api = new ApiClient(request);
    const user = await createUser(request);

    const createdProductResponse = await api.createProduct({
      name: 'Monitor',
      price: 900.0,
      userId: user.id,
    });

    expect(createdProductResponse.status()).toBe(200);
    const createdProduct = await createdProductResponse.json();

    const getResponse = await api.getProductById(createdProduct.id);
    expect(getResponse.status()).toBe(200);

    const body = await getResponse.json();
    expect(body.id).toBe(createdProduct.id);
    expect(body.name).toBe('Monitor');
    expect(body.userId).toBe(user.id);
  });
});