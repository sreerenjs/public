const { test } = require('@playwright/test');

// Global setup before each test
test.beforeEach(async ({ page, context }) => {
  // Set viewport
  await page.setViewportSize({ width: 1280, height: 720 });

  // Clear cookies
  await context.clearCookies();

  // Clear local storage
  await page.goto('about:blank'); // required before evaluating
  await page.evaluate(() => localStorage.clear());
});

// Example test
test('Login Automation Exercise', async ({ page }) => {
  console.log('Starting E2E Tests for Online Shopping');

  await page.goto('https://automationexercise.com/');

  // your test steps here
});