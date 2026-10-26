import { Locator, Page } from '@playwright/test';

export class SearchPage {
  readonly openSearchButton: Locator;
  readonly searchInput: Locator;
  readonly noResults: Locator;
  readonly basketCount: Locator;

  constructor(private readonly page: Page) {
    this.openSearchButton = page.getByRole('button', { name: 'Open search' });
    this.searchInput = page.locator('#searchQuery input');
    this.noResults = page.getByText('No results found');
    this.basketCount = page.getByRole('button', { name: 'Show the shopping cart' }).locator('.warn-notification');
  }

  async goto() {
    await this.page.goto('/#/search');
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
    await this.product(name).getByRole('button', { name: 'Add to Basket' }).click();
  }
}
