import { expect } from '@playwright/test';
import { test as base, createBdd } from 'playwright-bdd';
import { BasketPage } from '../pages/basket.page';
import { CheckoutPage } from '../pages/checkout.page';
import { HeaderComponent } from '../pages/header.component';
import { LoginPage } from '../pages/login.page';
import { RegisterPage } from '../pages/register.page';
import { SearchPage } from '../pages/search.page';

export const password = 'Password123';

type Credentials = { email: string; password: string };
type Session = { token: string; bid: number };
type CheckoutData = { fullName: string; cardEnding: string };

export const test = base.extend<{
  user: Credentials;
  newEmail: string;
  session: Session;
  checkoutData: CheckoutData;
  loginPage: LoginPage;
  registerPage: RegisterPage;
  searchPage: SearchPage;
  basketPage: BasketPage;
  checkoutPage: CheckoutPage;
  header: HeaderComponent;
}>({
  user: async ({ request }, use, testInfo) => {
    const email = `user${testInfo.parallelIndex}${Date.now()}@example.com`;

    const response = await request.post('/api/Users', {
      data: { email, password, passwordRepeat: password },
    });
    expect(response.status()).toBe(201);

    await use({ email, password });
  },

  newEmail: async ({}, use, testInfo) => {
    await use(`newuser${testInfo.parallelIndex}${Date.now()}@example.com`);
  },

  session: async ({ request, user }, use) => {
    const response = await request.post('/rest/user/login', {
      data: { email: user.email, password: user.password },
    });
    expect(response.status()).toBe(200);
    const { authentication } = await response.json();

    await use(authentication);
  },

  checkoutData: async ({ request, session }, use) => {
    const headers = { Authorization: `Bearer ${session.token}` };

    const address = await request.post('/api/Addresss', {
      headers,
      data: {
        fullName: 'Joao Silva',
        mobileNum: 1199998888,
        zipCode: '01001000',
        streetAddress: 'Rua A 10',
        city: 'Sao Paulo',
        state: 'SP',
        country: 'Brasil',
      },
    });
    expect(address.status()).toBe(201);

    const card = await request.post('/api/Cards', {
      headers,
      data: { fullName: 'Joao Silva', cardNum: 4111111111111111, expMonth: 12, expYear: 2090 },
    });
    expect(card.status()).toBe(201);

    await use({ fullName: 'Joao Silva', cardEnding: '1111' });
  },

  loginPage: async ({ page }, use) => use(new LoginPage(page)),
  registerPage: async ({ page }, use) => use(new RegisterPage(page)),
  searchPage: async ({ page }, use) => use(new SearchPage(page)),
  basketPage: async ({ page }, use) => use(new BasketPage(page)),
  checkoutPage: async ({ page }, use) => use(new CheckoutPage(page)),
  header: async ({ page }, use) => use(new HeaderComponent(page)),
});

export const { Given, When, Then } = createBdd(test);
