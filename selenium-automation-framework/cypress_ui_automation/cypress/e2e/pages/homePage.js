

class HomePage {
  // Selectors (identical to Playwright version)
  get PRODUCT_BUTTON() { return 'a[href="/products"]'; }
  get SEARCH_FIELD()   { return 'input[placeholder="Search Product"]'; }
  get SEARCH_BUTTON()  { return 'button[id="submit_search"]'; }

   
  searchProduct(product) {
    cy.get(this.PRODUCT_BUTTON).click();
    cy.get(this.SEARCH_FIELD).first().clear().type(product);
    cy.get(this.SEARCH_BUTTON).first().click();
  }
 
  selectProduct(product) {
    // Find the product card containing the product name, then click Add to Cart
    cy.contains('.single-products', product)
      .find('a.add-to-cart')
      .first()
      .click();

    
    cy.contains('View Cart').click();
  }
}

module.exports = HomePage;
