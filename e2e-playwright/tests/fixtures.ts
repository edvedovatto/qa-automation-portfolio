import { test as base, expect, Page } from '@playwright/test';

type Credentials = { email: string; password: string };

export const test = base.extend<{ user: Credentials; loggedInPage: Page }>({
  user: async ({ request }, use) => {
    const email = `user${Date.now()}@gmail.com`;
    const password = 'Password123';

    const response = await request.post('/api/Users', {
      data: { email, password, passwordRepeat: password },
    });
    expect(response.status()).toBe(201);

    await use({ email, password });
  },

  loggedInPage: async ({ page, request, user }, use) => {
    const response = await request.post('/rest/user/login', {
      data: { email: user.email, password: user.password },
    });
    expect(response.status()).toBe(200);
    const { authentication } = await response.json();

    await page.addInitScript(({ token, bid }) => {
      localStorage.setItem('token', token);
      sessionStorage.setItem('bid', bid);
    }, authentication);

    await use(page);
  },
});

export { expect };
