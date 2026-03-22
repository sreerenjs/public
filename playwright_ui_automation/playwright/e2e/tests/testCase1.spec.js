const { test } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

const LoginPage = require('../pages/loginPage');
const HomePage = require('../pages/homePage');

let testData;

test.describe('AutomationExercise', () => {

  test.beforeAll(() => {
    const filePath = path.resolve(__dirname, '../../../fixtures/testData.json');
    testData = JSON.parse(fs.readFileSync(filePath, 'utf-8'));
  });

  test('Login Automation Exercise', async ({ page }) => {
    await page.goto(testData.appUrl.url);

    const loginPage = new LoginPage(page);

    const homePage = new HomePage(page);

    await loginPage.login(testData.prodCredentials.username, testData.prodCredentials.password);

    await homePage.searchProduct(testData.project.product);

    await homePage.selectProduct(testData.project.product);

  });

});
