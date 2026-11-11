import { defineConfig } from '@playwright/test';
import { defineBddConfig } from 'playwright-bdd';

const testDir = defineBddConfig({
  features: 'features/*.feature',
  steps: 'steps/*.ts',
  examplesTitleFormat: 'when <problem>',
});

const baseURL = process.env.BASE_URL ?? 'http://localhost:3000';

const dismissedBanners = ['welcomebanner_status', 'cookieconsent_status'].map((name) => ({
  name,
  value: 'dismiss',
  domain: new URL(baseURL).hostname,
  path: '/',
  expires: -1,
  httpOnly: false,
  secure: false,
  sameSite: 'Lax' as const,
}));

export default defineConfig({
  testDir,
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: process.env.CI
    ? [['list'], ['github'], ['html', { open: 'never' }], ['junit', { outputFile: 'test-results/junit.xml' }]]
    : 'list',
  use: {
    baseURL,
    storageState: { cookies: dismissedBanners, origins: [] },
    trace: 'retain-on-failure',
  },
  projects: [{ name: 'chromium', use: { browserName: 'chromium' } }],
});
