import { expect } from '@playwright/test';
import { Given, Then, When } from './fixtures';

Given('I have an account', async ({ user: _user }) => {});

Given('I am a logged in customer', async ({ page, session }) => {
  await page.addInitScript(({ token, bid }) => {
    localStorage.setItem('token', token);
    sessionStorage.setItem('bid', String(bid));
  }, session);
});

Given('I am on the login page', async ({ loginPage }) => {
  await loginPage.goto();
});

Given('I am on the login page coming from the basket', async ({ loginPage }) => {
  await loginPage.goto('/basket');
});

Given('I have logged in with my credentials', async ({ page, loginPage, user }) => {
  await loginPage.goto();
  await loginPage.login(user.email, user.password);
  await expect(page).toHaveURL(/#\/search/);
});

When('I log in with my credentials', async ({ loginPage, user }) => {
  await loginPage.login(user.email, user.password);
});

When('I log in with the password {string}', async ({ loginPage, user }, wrongPassword: string) => {
  await loginPage.login(user.email, wrongPassword);
});

When('I log in as {string} with the password {string}', async ({ loginPage }, email: string, password: string) => {
  await loginPage.login(email, password);
});

When('I reload the page', async ({ page }) => {
  await page.reload();
});

When('I log out', async ({ header }) => {
  await header.logout();
});

Then('I am taken to the shop', async ({ page }) => {
  await expect(page).toHaveURL(/#\/search/);
});

Then('I am taken to the basket', async ({ page }) => {
  await expect(page).toHaveURL(/#\/basket/);
});

Then('I am logged in', async ({ header, user }) => {
  await header.expectLoggedInAs(user.email);
});

Then('I am not logged in', async ({ header }) => {
  await header.expectLoggedOut();
});

Then('I see the login error {string}', async ({ loginPage }, message: string) => {
  await expect(loginPage.error).toHaveText(message);
});

Then('I stay on the login page', async ({ page }) => {
  await expect(page).toHaveURL(/#\/login/);
});

Then('the login button is disabled', async ({ loginPage }) => {
  await expect(loginPage.loginButton).toBeDisabled();
});
