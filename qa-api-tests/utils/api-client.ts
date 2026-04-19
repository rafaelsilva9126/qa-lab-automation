import { APIRequestContext, APIResponse } from '@playwright/test';

export class ApiClient {
  constructor(private readonly request: APIRequestContext) {}

  async createUser(payload: { email: string; password: string }): Promise<APIResponse> {
    return this.request.post('/users', { data: payload });
  }

  async getUsers(): Promise<APIResponse> {
    return this.request.get('/users');
  }

  async createProduct(payload: {
    name: string;
    price: number;
    userId: number;
  }): Promise<APIResponse> {
    return this.request.post('/products', { data: payload });
  }

  async getProducts(): Promise<APIResponse> {
    return this.request.get('/products');
  }

  async getProductById(id: number): Promise<APIResponse> {
    return this.request.get(`/products/${id}`);
  }
}