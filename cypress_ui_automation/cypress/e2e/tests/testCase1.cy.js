
const LoginPage = require('../pages/loginPage');
const HomePage = require('../pages/homePage');

describe('AutomationExercise', () => {

  
  before(() => {
    cy.fixture('testData').as('testData');
  });

  
  it('Login Automation Exercise', function () {
    const { appUrl, prodCredentials, project } = this.testData;

    const loginPage = new LoginPage();
    const homePage = new HomePage();

    
    cy.visit(appUrl.url);
 
    loginPage.login(prodCredentials.username, prodCredentials.password);

    
    homePage.searchProduct(project.product);

     
    homePage.selectProduct(project.product);
  });

});
