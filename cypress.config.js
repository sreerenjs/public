const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {

    specPattern: 'cypress/e2e/tests/**/*.cy.js',

    supportFile: 'cypress/support/e2e.js',

    fixturesFolder: 'cypress/fixtures',

    screenshotsFolder: 'cypress/screenshots',

    videosFolder: 'cypress/videos',
    video: true,


    defaultCommandTimeout: 30000,
    pageLoadTimeout: 30000,


    retries: {
      runMode: 0,
      openMode: 0,
    },

    reporter: 'mochawesome',
    reporterOptions: {
      reportDir: 'cypress/reports',
      overwrite: false,
      html: true,
      json: true,
    },

    setupNodeEvents(on, config) {
      // Node event listeners can be added here
      return config;
    },
  },


  viewportWidth: 1280,
  viewportHeight: 720,


});
