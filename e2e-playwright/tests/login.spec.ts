import { test, expect } from './fixtures';
import { LoginPage } from '../pages/login.page';

test.describe('Login', () => {
  let loginPage: LoginPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
  });

  test('valid credentials land on the shop and show the user', async ({ page, user }) => {
    await loginPage.login(user.email, user.password);

    await expect(page).toHaveURL(/#\/search/);
    await loginPage.expectLoggedInAs(user.email);
  });

  test('wrong password shows an error and keeps the user logged out', async ({ page, user }) => {
    await loginPage.login(user.email, 'wrongpassword');

    await expect(loginPage.error).toHaveText('Invalid email or password.');
    await expect(page).toHaveURL(/#\/login/);
    expect(await loginPage.token()).toBeNull();
  });

  test('unknown email shows the same error as a wrong password', async ({ page }) => {
    await loginPage.login('wrong@gmail.com', 'wrongpassword');

    await expect(loginPage.error).toHaveText('Invalid email or password.');
    await expect(page).toHaveURL(/#\/login/);
  });

  test('login button is disabled while the form is empty', async () => {
    await expect(loginPage.loginButton).toBeDisabled();
  });

  test('stays logged in after a page reload', async ({ page, user }) => {
    await loginPage.login(user.email, user.password);
    await expect(page).toHaveURL(/#\/search/);

    await page.reload();

    await loginPage.expectLoggedInAs(user.email);
  });

  test('logout ends the session', async ({ page, user }) => {
    await loginPage.login(user.email, user.password);
    await expect(page).toHaveURL(/#\/search/);

    await loginPage.logout();

    await loginPage.expectLoggedOut();
  });

  test('goes back to the page the user was sent from', async ({ page, user }) => {
    await loginPage.goto('/basket');
    await loginPage.login(user.email, user.password);

    await expect(page).toHaveURL(/#\/basket/);
  });
});
