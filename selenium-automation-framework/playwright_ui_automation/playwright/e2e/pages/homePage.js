const { expect } = require('@playwright/test');

class HomePage {
  PRODUCT_BUTTON = 'a[href="/products"]';
  SEARCH_FIELD = 'input[placeholder="Search Product"]';
  SEARCH_BUTTON = 'button[id="submit_search"]';
  constructor(page) {
    this.page = page;
  }

  async searchProduct(product) {
    // Language selection
    await this.page.click(this.PRODUCT_BUTTON);
    await this.page.locator(this.SEARCH_FIELD).first().fill(product);
  await this.page.locator(this.SEARCH_BUTTON).first().click();
  }

  async selectProduct(product) {


    await this.page
    .locator(`.single-products:has(p:has-text("${product}")) a.add-to-cart`).first().click();
/*
 await this.page.locator(
  `(//div[contains(@class,'single-products')]
     [.//p[normalize-space()='${product}']]
     //a[contains(@class,'add-to-cart')]
   )[1]`
).click();
  */

//await this.page.pause();

  // 4. Go to cart (if modal appears)
  await this.page.getByText('View Cart').click();


}

}

module.exports = HomePage;