import { expect } from '@playwright/test';
import { Given, Then, When } from './fixtures';

Given('I am on the shop', async ({ searchPage }) => {
  await searchPage.goto();
});

When('I search for {string}', async ({ searchPage }, term: string) => {
  await searchPage.search(term);
});

When('I add {string} to the basket', async ({ searchPage }, product: string) => {
  await searchPage.addToBasket(product);
});

When('I open the basket', async ({ basketPage }) => {
  await basketPage.goto();
});

When('I remove {string}', async ({ basketPage }, product: string) => {
  await basketPage.remove(product);
});

Then('I see the product {string}', async ({ searchPage }, product: string) => {
  await expect(searchPage.product(product)).toBeVisible();
});

Then('I do not see the product {string}', async ({ searchPage }, product: string) => {
  await expect(searchPage.product(product)).toHaveCount(0);
});

Then('I see that no results were found', async ({ searchPage }) => {
  await expect(searchPage.noResults).toBeVisible();
});

Then('the basket has {int} {string}', async ({ basketPage }, quantity: number, product: string) => {
  await basketPage.expectItem(product, quantity);
});

Then('the basket is empty', async ({ basketPage }) => {
  await basketPage.expectEmpty();
});

Then('the checkout button is disabled', async ({ basketPage }) => {
  await expect(basketPage.checkoutButton).toBeDisabled();
});
