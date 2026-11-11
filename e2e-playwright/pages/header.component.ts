import { expect, type Locator, type Page } from '@playwright/test';

export class HeaderComponent {
  readonly basketCount: Locator;
  private readonly accountMenu: Locator;

  constructor(private readonly page: Page) {
    this.basketCount = page.getByRole('button', { name: 'Show the shopping cart' }).locator('.warn-notification');
    this.accountMenu = page.getByRole('button', { name: 'Show/hide account menu' });
  }

  async logout() {
    await this.accountMenu.click();
    await this.page.getByRole('menuitem', { name: 'Logout' }).click();
  }

  async expectLoggedInAs(email: string) {
    await this.accountMenu.click();
    await expect(this.page.getByRole('menuitem', { name: 'Go to user profile' })).toContainText(email);
  }

  async expectLoggedOut() {
    await this.accountMenu.click();
    await expect(this.page.getByRole('menuitem', { name: 'Go to login page' })).toBeVisible();
  }
}
