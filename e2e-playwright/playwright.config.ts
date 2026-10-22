import { defineConfig } from '@playwright/test';

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
  testDir: './tests',
  use: {
    baseURL,
    storageState: { cookies: dismissedBanners, origins: [] },
    trace: 'retain-on-failure',
  },
  projects: [{ name: 'chromium', use: { browserName: 'chromium' } }],
});
