import { Page, expect } from '@playwright/test';

export class BasketPage {
  constructor(private readonly page: Page) {}

  async goto() {
    await this.page.goto('/#/basket');
  }

  item(name: string) {
    return this.page.locator('mat-row', { hasText: name });
  }

  async expectItem(name: string, quantity: number) {
    await expect(this.item(name)).toBeVisible();
    await expect(this.item(name).locator('mat-cell').nth(2)).toHaveText(String(quantity));
  }

  async remove(name: string) {
    await this.item(name).locator('.fa-trash-alt').click();
  }

  async expectEmpty() {
    await expect(this.page.locator('mat-row')).toHaveCount(0);
  }
}
