import { expect, type Locator, type Page } from '@playwright/test';

export class BasketPage {
  readonly checkoutButton: Locator;
  private readonly rows: Locator;

  constructor(private readonly page: Page) {
    this.checkoutButton = page.getByRole('button', { name: 'Checkout' });
    this.rows = page.getByRole('row').filter({ has: page.getByRole('cell') });
  }

  async goto() {
    await this.page.goto('/#/basket');
  }

  item(name: string) {
    return this.rows.filter({ hasText: name });
  }

  async expectItem(name: string, quantity: number) {
    await expect(this.item(name)).toBeVisible();
    await expect(this.item(name).locator('.mat-column-quantity')).toHaveText(String(quantity));
  }

  async remove(name: string) {
    await this.item(name).locator('.mat-column-remove').getByRole('button').click();
  }

  async expectEmpty() {
    await expect(this.rows).toHaveCount(0);
  }

  async checkout() {
    await this.checkoutButton.click();
  }
}
