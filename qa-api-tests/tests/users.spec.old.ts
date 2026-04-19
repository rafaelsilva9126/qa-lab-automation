import { test, expect } from '@playwright/test';

test.describe('Users API', () => {
  test('should create a user successfully', async ({ request }) => {
    const response = await request.post('/users', {
      data: {
        email: `user_${Date.now()}@mail.com`,
        password: '123456',
      },
    });

    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(body.id).toBeDefined();
    expect(body.email).toContain('@mail.com');
    expect(body.password).toBeUndefined();
  });

  test('should return validation error for invalid email', async ({ request }) => {
    const response = await request.post('/users', {
      data: {
        email: 'invalid',
        password: '123456',
      },
    });

    expect(response.status()).toBe(400);

    const body = await response.text();
    expect(body).toContain('Email must be valid');
  });

  test('should get all users', async ({ request }) => {
    const response = await request.get('/users');

    expect(response.status()).toBe(200);

    const body = await response.json();
    expect(Array.isArray(body)).toBeTruthy();
  });
});