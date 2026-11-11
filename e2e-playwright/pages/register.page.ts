import type { Locator, Page } from '@playwright/test';

export class RegisterPage {
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly repeatPasswordInput: Locator;
  readonly securityQuestion: Locator;
  readonly securityAnswer: Locator;
  readonly registerButton: Locator;
  readonly error: Locator;
  readonly fieldErrors: Locator;

  constructor(private readonly page: Page) {
    this.emailInput = page.getByRole('textbox', { name: 'Email address field' });
    this.passwordInput = page.getByRole('textbox', { name: 'Field for the password' });
    this.repeatPasswordInput = page.getByRole('textbox', { name: 'Field to confirm the password' });
    this.securityQuestion = page.getByRole('combobox', { name: 'Selection list for the security question' });
    this.securityAnswer = page.getByRole('textbox', { name: 'Field for the answer to the security question' });
    this.registerButton = page.getByRole('button', { name: 'Button to complete the registration' });
    this.error = page.getByRole('alert');
    this.fieldErrors = page.locator('mat-error');
  }

  async goto() {
    const securityQuestions = this.page.waitForResponse(
      (response) => response.url().includes('/api/SecurityQuestions') && response.ok(),
    );
    await this.page.goto('/#/register');
    await securityQuestions;
  }

  async fillCredentials(email: string, password: string, repeatPassword = password) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.repeatPasswordInput.fill(repeatPassword);
  }

  async leaveForm() {
    await this.repeatPasswordInput.blur();
    await this.emailInput.blur();
  }

  async register(email: string, password: string) {
    await this.fillCredentials(email, password);
    await this.securityQuestion.click();
    await this.page.getByRole('option').first().click();
    await this.securityAnswer.fill('Maria');
    await this.registerButton.click();
  }
}
