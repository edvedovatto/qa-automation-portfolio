import { test, expect } from './fixtures';
import { SearchPage } from '../pages/search.page';
import { BasketPage } from '../pages/basket.page';

test.describe('Basket', () => {
  test('adding a product puts it in the basket', async ({ loggedInPage }) => {
    const searchPage = new SearchPage(loggedInPage);
    const basketPage = new BasketPage(loggedInPage);

    await searchPage.goto();
    await searchPage.addToBasket('Apple Juice (1000ml)');
    await expect(searchPage.basketCount).toHaveText('1');
    await basketPage.goto();

    await basketPage.expectItem('Apple Juice (1000ml)', 1);
  });

  test('adding the same product twice increases the quantity', async ({ loggedInPage }) => {
    const searchPage = new SearchPage(loggedInPage);
    const basketPage = new BasketPage(loggedInPage);

    await searchPage.goto();
    await searchPage.addToBasket('Apple Juice (1000ml)');
    await expect(searchPage.basketCount).toHaveText('1');
    await searchPage.addToBasket('Apple Juice (1000ml)');
    await expect(searchPage.basketCount).toHaveText('2');
    await basketPage.goto();

    await basketPage.expectItem('Apple Juice (1000ml)', 2);
  });

  test('removing the only product empties the basket', async ({ loggedInPage }) => {
    const searchPage = new SearchPage(loggedInPage);
    const basketPage = new BasketPage(loggedInPage);

    await searchPage.goto();
    await searchPage.addToBasket('Apple Juice (1000ml)');
    await expect(searchPage.basketCount).toHaveText('1');
    await basketPage.goto();
    await basketPage.remove('Apple Juice (1000ml)');

    await basketPage.expectEmpty();
  });
});
