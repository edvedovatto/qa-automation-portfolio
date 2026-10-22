import { test as base, expect } from '@playwright/test';

type Credentials = { email: string; password: string };

export const test = base.extend<{ user: Credentials }>({
  user: async ({ request }, use) => {
    const email = `user${Date.now()}@gmail.com`;
    const password = 'Password123';

    const response = await request.post('/api/Users', {
      data: { email, password, passwordRepeat: password },
    });
    expect(response.status()).toBe(201);

    await use({ email, password });
  },
});

export { expect };
