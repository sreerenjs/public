 
// Import custom Cypress commands
import './commands';

// Optionally load environment variables from a .env file
// Install dotenv: npm install dotenv --save-dev
try {
  require('dotenv').config();
} catch (e) {
  // dotenv is optional; continue without it if not installed
}

 
beforeEach(() => {
  cy.viewport(1280, 720);   
  cy.clearCookies();        
  cy.clearLocalStorage();   
});

 
Cypress.on('uncaught:exception', (err) => {
  console.log('Uncaught exception:', err.message);
  return false; // Return false to prevent Cypress from failing the test
});

// ─── Global Before Hook ───────────────────────────────────────────────────────
// Runs once before the entire test suite (all spec files).
before(() => {
  cy.log('Starting E2E Tests for Online Shopping');
});
