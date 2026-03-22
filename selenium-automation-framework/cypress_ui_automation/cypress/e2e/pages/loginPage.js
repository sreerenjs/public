 
class LoginPage {
  // Selectors (identical to Playwright version)
  get SIGNUP_LOGIN_FIELD() { return 'a[href="/login"]'; }
  get EMAIL_FIELD()        { return 'input[placeholder="Email Address"]'; }
  get PASSWORD_FIELD()     { return 'input[placeholder="Password"]'; }
  get LOGIN_BUTTON()       { return 'button[type="submit"]'; }
  get CART_BUTTON()        { return 'a[href="/view_cart"]'; }

   
  login(username, password) {
    cy.get(this.SIGNUP_LOGIN_FIELD).click();
    cy.get(this.EMAIL_FIELD).first().clear().type(username);
    cy.get(this.PASSWORD_FIELD).clear().type(password);
    cy.get(this.LOGIN_BUTTON).click();
  }

   
  verifyLoginPage() {
    cy.get(this.CART_BUTTON).first().should('be.visible');
  }
}

module.exports = LoginPage;
