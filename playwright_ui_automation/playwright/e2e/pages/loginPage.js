const { expect } = require('@playwright/test');

class LoginPage {
  SIGNUP_LOGIN_FIELD = 'a[href="/login"]';
  EMAIL_FIELD = 'input[placeholder="Email Address"]';
  PASSWORD_FIELD = 'input[placeholder="Password"]';
  LOGIN_BUTTON = 'button[type="submit"]';
  CART_BUTTON = 'a[href="/view_cart"]';


  constructor(page) {
    this.page = page;
  }

  async login(username, password) {
    await this.page.click(this.SIGNUP_LOGIN_FIELD);
    await this.page.locator(this.EMAIL_FIELD).first().fill(username);
    await this.page.locator(this.PASSWORD_FIELD).fill(password);
    await this.page.click(this.LOGIN_BUTTON);

  }


  async verifyLoginPage() {
    await expect(this.page.locator(this.CART_BUTTON).first()).toBeVisible();
  }


}

module.exports = LoginPage;