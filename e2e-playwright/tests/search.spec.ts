import { test, expect } from './fixtures';
import { SearchPage } from '../pages/search.page';

test.describe('Search', () => {
  let searchPage: SearchPage;

  test.beforeEach(async ({ page }) => {
    searchPage = new SearchPage(page);
    await searchPage.goto();
  });

  test('shows only the products that match', async () => {
    await searchPage.search('apple');

    await expect(searchPage.product('Apple Juice (1000ml)')).toBeVisible();
    await expect(searchPage.product('Banana Juice (1000ml)')).toHaveCount(0);
  });

  test('shows a message when nothing matches', async () => {
    await searchPage.search('zzzz');

    await expect(searchPage.noResults).toBeVisible();
  });
});
