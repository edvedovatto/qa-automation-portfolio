import type { Locator, Page } from '@playwright/test';

export class LoginPage {
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly error: Locator;

  constructor(private readonly page: Page) {
    this.emailInput = page.getByLabel('Text field for the login email');
    this.passwordInput = page.getByLabel('Text field for the login password');
    this.loginButton = page.getByRole('button', { name: 'Login', exact: true });
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
}
