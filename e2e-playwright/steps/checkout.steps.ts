import { expect } from '@playwright/test';
import { Given, Then, When } from './fixtures';

Given('I have a saved address and card', async ({ checkoutData: _checkoutData }) => {});

Given('I start the checkout', async ({ basketPage }) => {
  await basketPage.goto();
  await basketPage.checkout();
});

When('I choose my address, standard delivery and my card', async ({ checkoutPage, checkoutData }) => {
  await checkoutPage.chooseAddress(checkoutData.fullName);
  await checkoutPage.chooseDelivery('Standard Delivery');
  await checkoutPage.chooseCard(checkoutData.cardEnding);
});

When('I choose my address and standard delivery', async ({ checkoutPage, checkoutData }) => {
  await checkoutPage.chooseAddress(checkoutData.fullName);
  await checkoutPage.chooseDelivery('Standard Delivery');
});

When('I place the order', async ({ checkoutPage }) => {
  await checkoutPage.placeOrder();
});

Then('I see the order confirmation', async ({ checkoutPage }) => {
  await checkoutPage.expectOrderPlaced();
});

Then('the basket counter goes back to 0', async ({ header }) => {
  await expect(header.basketCount).toHaveText('0');
});

Then('I see my saved address', async ({ checkoutPage, checkoutData }) => {
  await expect(checkoutPage.addressRow(checkoutData.fullName)).toBeVisible();
});

Then('I cannot continue without choosing an address', async ({ checkoutPage }) => {
  await expect(checkoutPage.addressContinue).toBeDisabled();
});

Then('I see my saved card', async ({ checkoutPage, checkoutData }) => {
  await expect(checkoutPage.cardRow(checkoutData.cardEnding)).toBeVisible();
});

Then('I cannot continue without choosing a card', async ({ checkoutPage }) => {
  await expect(checkoutPage.cardContinue).toBeDisabled();
});
