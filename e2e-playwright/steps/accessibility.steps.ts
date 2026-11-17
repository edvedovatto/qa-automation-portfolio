import AxeBuilder from '@axe-core/playwright';
import { expect } from '@playwright/test';
import { Then } from './fixtures';

const wcag = ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'];

const knownProblems = [
  'label',
  'button-name',
  'label-content-name-mismatch',
  'aria-required-children',
  'color-contrast',
];

Then('the page has no new accessibility problems', async ({ page, $testInfo }) => {
  const { violations } = await new AxeBuilder({ page }).withTags(wcag).disableRules(knownProblems).analyze();

  if (violations.length > 0) {
    await $testInfo.attach('accessibility-violations', {
      body: JSON.stringify(violations, null, 2),
      contentType: 'application/json',
    });
  }
  expect(violations.map((violation) => `${violation.id}: ${violation.help}`)).toEqual([]);
});
