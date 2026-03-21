const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './playwright/e2e/ui/tests',
  timeout: 30 * 1000,
  retries: 0,

  reporter: [['html', { open: 'never' }]],

  use: {
    headless: false,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
   // trace: 'on-first-retry',
   trace: 'on',
  },
});
