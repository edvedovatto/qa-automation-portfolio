import { Locator, Page, expect } from '@playwright/test';

export class LoginPage {
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly error: Locator;

  constructor(private readonly page: Page) {
    this.emailInput = page.locator('#email');
    this.passwordInput = page.locator('#password');
    this.loginButton = page.locator('#loginButton');
    this.error = page.getByRole('alert');
  }

  async goto(redirectUrl?: string) {
    await this.page.goto(redirectUrl ? `/#/login?redirectUrl=${redirectUrl}` : '/#/login');
  }

  async login(email: string, password: string) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
  }

  async logout() {
    await this.page.locator('#navbarAccount').click();
    await this.page.locator('#navbarLogoutButton').click();
  }

  async token() {
    return this.page.evaluate(() => localStorage.getItem('token'));
  }

  async expectLoggedInAs(email: string) {
    await this.page.locator('#navbarAccount').click();
    await expect(this.page.getByRole('menuitem', { name: 'Go to user profile' })).toContainText(email);
  }

  async expectLoggedOut() {
    await this.page.locator('#navbarAccount').click();
    await expect(this.page.locator('#navbarLoginButton')).toBeVisible();
    expect(await this.token()).toBeNull();
  }
}
