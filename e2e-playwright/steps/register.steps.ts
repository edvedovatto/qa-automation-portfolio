import { expect } from '@playwright/test';
import { Given, password, Then, When } from './fixtures';

Given('I am on the registration page', async ({ registerPage }) => {
  await registerPage.goto();
});

Given('I have answered the security question', async ({ registerPage }) => {
  await registerPage.answerSecurityQuestion();
});

When('I register with a new email', async ({ registerPage, newEmail }) => {
  await registerPage.register(newEmail, password);
});

When('I register with my email', async ({ registerPage, user }) => {
  await registerPage.register(user.email, password);
});

When(
  'I fill in the email {string}, the password {string} and the repeated password {string}',
  async ({ registerPage }, email: string, typedPassword: string, repeatedPassword: string) => {
    await registerPage.fillCredentials(email, typedPassword, repeatedPassword);
    await registerPage.leaveForm();
  },
);

Then('I am taken to the login page', async ({ page }) => {
  await expect(page).toHaveURL(/#\/login/);
});

Then('I can log in with the new account', async ({ loginPage, header, newEmail }) => {
  await loginPage.login(newEmail, password);
  await header.expectLoggedInAs(newEmail);
});

Then('I see the registration error {string}', async ({ registerPage }, message: string) => {
  await expect(registerPage.error).toHaveText(message);
});

Then('I am still on the registration page', async ({ page }) => {
  await expect(page).toHaveURL(/#\/register/);
});

Then('I see the field error {string}', async ({ registerPage }, message: string) => {
  await expect(registerPage.fieldErrors).toHaveText(message);
});

Then('the register button is disabled', async ({ registerPage }) => {
  await expect(registerPage.registerButton).toBeDisabled();
});
