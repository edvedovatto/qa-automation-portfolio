import { expect, type Locator, type Page } from '@playwright/test';

export class CheckoutPage {
  readonly addressContinue: Locator;
  readonly deliveryContinue: Locator;
  readonly cardContinue: Locator;

  constructor(private readonly page: Page) {
    this.addressContinue = page.getByRole('button', { name: 'Proceed to payment selection' });
    this.deliveryContinue = page.getByRole('button', { name: 'Proceed to delivery method selection' });
    this.cardContinue = page.getByRole('button', { name: 'Proceed to review' });
  }

  addressRow(name: string) {
    return this.page.getByRole('row', { name });
  }

  cardRow(cardEnding: string) {
    return this.page.getByRole('row', { name: `************${cardEnding}` });
  }

  async chooseAddress(name: string) {
    await this.addressRow(name).getByRole('radio').check();
    await this.addressContinue.click();
  }

  async chooseDelivery(name: string) {
    await this.page.getByRole('row', { name }).getByRole('radio').check();
    await this.deliveryContinue.click();
  }

  async chooseCard(cardEnding: string) {
    await this.cardRow(cardEnding).getByRole('radio').check();
    await this.cardContinue.click();
  }

  async placeOrder() {
    await this.page.getByRole('button', { name: 'Complete your purchase' }).click();
  }

  async expectOrderPlaced() {
    await expect(this.page.getByRole('heading', { name: 'Thank you for your purchase!' })).toBeVisible();
  }
}
