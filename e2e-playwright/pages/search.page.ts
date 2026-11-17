import { expect, type Locator, type Page } from '@playwright/test';

export class SearchPage {
  readonly openSearchButton: Locator;
  readonly searchInput: Locator;
  readonly noResults: Locator;

  constructor(private readonly page: Page) {
    this.openSearchButton = page.getByRole('button', { name: 'Open search' });
    this.searchInput = page.locator('#searchQuery input');
    this.noResults = page.getByText('No results found');
  }

  async goto() {
    await this.page.goto('/#/search');
    await expect(this.page.locator('app-product').first()).toBeVisible();
  }

  async search(term: string) {
    await this.openSearchButton.click();
    await this.searchInput.fill(term);
    await this.searchInput.press('Enter');
  }

  product(name: string) {
    return this.page.locator('app-product').filter({ has: this.page.getByText(name, { exact: true }) });
  }

  async addToBasket(name: string) {
    const saved = this.page.waitForResponse(
      (response) =>
        response.url().includes('/api/BasketItems') &&
        ['POST', 'PUT'].includes(response.request().method()) &&
        response.ok(),
    );
    await this.product(name).getByRole('button', { name: 'Add to Basket' }).click();
    await saved;
  }

  async addToBasketAsVisitor(name: string, basketCount: Locator) {
    const before = Number((await basketCount.textContent({ timeout: 1000 }).catch(() => null)) ?? 0);
    await this.product(name).getByRole('button', { name: 'Add to Basket' }).click();
    await expect(basketCount).toHaveText(String(before + 1));
  }
}
