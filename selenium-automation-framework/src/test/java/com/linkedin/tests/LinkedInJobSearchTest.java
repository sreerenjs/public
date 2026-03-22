package com.linkedin.tests;

import com.aventstack.extentreports.Status;
import com.linkedin.base.BaseTest;
import com.linkedin.config.ConfigReader;
import com.linkedin.pages.LinkedInHomePage;
import com.linkedin.pages.LinkedInJobsPage;
import com.linkedin.pages.LinkedInLoginPage;
import com.linkedin.utils.ExcelReader;
import com.linkedin.utils.ExtentReportManager;
import com.linkedin.utils.LinkedInApiClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * LinkedInJobSearchTest
 *
 * Test suite covering:
 *  TC001 - Login with valid credentials
 *  TC002 - Navigate to Jobs page
 *  TC003 - Search for jobs (data driven from Excel)
 *  TC004 - Verify job results are displayed
 *  TC005 - Apply experience-level filter
 *  TC006 - API: Verify LinkedIn API is reachable
 *  TC007 - API: Validate job search API returns 200
 */
public class LinkedInJobSearchTest extends BaseTest {

    private LinkedInLoginPage loginPage;
    private LinkedInHomePage  homePage;
    private LinkedInJobsPage  jobsPage;
    private ExcelReader       excelReader;

    // ─── Class-level setup ────────────────────────────────────────────────────

    @BeforeClass
    public void initPages() {
        excelReader = new ExcelReader();
    }

    @AfterClass
    public void closeExcel() {
        if (excelReader != null) excelReader.closeWorkbook();
    }

    // ─── Data Provider (reads from Excel) ────────────────────────────────────

    /**
     * Supplies { jobTitle, location, experienceLevel } rows from Excel
     * Maps to TC003 - data-driven job search
     */
    @DataProvider(name = "jobSearchData")
    public Object[][] jobSearchDataProvider() {
        List<Map<String, String>> rows = excelReader.getAllRows();
        Object[][] data = new Object[rows.size()][3];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i).getOrDefault("jobTitle",        "Java Developer");
            data[i][1] = rows.get(i).getOrDefault("location",        "Bangalore");
            data[i][2] = rows.get(i).getOrDefault("experienceLevel", "Mid-Senior level");
        }
        return data;
    }

    // ─── Test Cases ──────────────────────────────────────────────────────────

    /**
     * TC001 - Login with valid credentials
     */
    @Test(priority = 1, description = "TC001 - Login to LinkedIn with valid credentials")
    public void tc001_loginWithValidCredentials() {
        ExtentReportManager.getTest().log(Status.INFO, "TC001: Starting login test");

        // Navigate to the app
        getDriver().get(ConfigReader.getAppUrl());
        ExtentReportManager.getTest().log(Status.INFO,
            "Navigated to: " + ConfigReader.getAppUrl());

        // Instantiate Login page and perform login
        loginPage = new LinkedInLoginPage(getDriver(), wait);
        homePage  = loginPage.login(
            ConfigReader.getUsername(),
            ConfigReader.getPassword()
        );

        ExtentReportManager.getTest().log(Status.INFO,
            "Login attempted with: " + ConfigReader.getUsername());

        // Assertion
        Assert.assertTrue(homePage.isLoggedIn(),
            "Login failed — profile icon not visible after login.");

        ExtentReportManager.getTest().log(Status.PASS, "TC001: Login successful");
    }

    /**
     * TC002 - Navigate to the Jobs section
     */
    @Test(priority = 2, description = "TC002 - Navigate to LinkedIn Jobs page",
          dependsOnMethods = "tc001_loginWithValidCredentials")
    public void tc002_navigateToJobsPage() {
        ExtentReportManager.getTest().log(Status.INFO, "TC002: Navigating to Jobs section");

        jobsPage = homePage.navigateToJobs();

        Assert.assertTrue(jobsPage.isOnJobsPage(),
            "Jobs page was not loaded — URL does not contain '/jobs'.");

        ExtentReportManager.getTest().log(Status.PASS,
            "TC002: Successfully navigated to Jobs page");
    }

    /**
     * TC003 - Data-driven job search from Excel
     */
    @Test(priority = 3, description = "TC003 - Search for jobs using data from Excel",
          dataProvider = "jobSearchData",
          dependsOnMethods = "tc002_navigateToJobsPage")
    public void tc003_searchForJobs(String jobTitle, String location, String experienceLevel) {
        ExtentReportManager.getTest().log(Status.INFO,
            String.format("TC003: Searching — Title: '%s' | Location: '%s' | Level: '%s'",
                jobTitle, location, experienceLevel));

        jobsPage = jobsPage.searchJobs(jobTitle, location);

        String resultCount = jobsPage.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO,
            "Search results count: " + resultCount);

        List<String> titles = jobsPage.getJobTitles();
        Assert.assertFalse(titles.isEmpty(),
            "No job results found for: " + jobTitle + " in " + location);

        ExtentReportManager.getTest().log(Status.PASS,
            "TC003: Job search returned " + titles.size() + " results");

        // Log first 3 job titles to report
        titles.stream().limit(3).forEach(title ->
            ExtentReportManager.getTest().log(Status.INFO, "Job found: " + title)
        );
    }

    /**
     * TC004 - Verify job result cards are displayed
     */
    @Test(priority = 4, description = "TC004 - Verify job result cards contain title and company",
          dependsOnMethods = "tc003_searchForJobs")
    public void tc004_verifyJobResultCards() {
        ExtentReportManager.getTest().log(Status.INFO,
            "TC004: Verifying job result card details");

        List<String[]> results = jobsPage.getJobResults();
        Assert.assertFalse(results.isEmpty(), "No job result cards found on page");

        // Check first card has a title
        String[] firstJob = results.get(0);
        Assert.assertNotEquals(firstJob[0], "N/A",
            "First job card title is empty");

        ExtentReportManager.getTest().log(Status.INFO,
            String.format("First result — Title: %s | Company: %s | Location: %s",
                firstJob[0], firstJob[1], firstJob[2]));

        ExtentReportManager.getTest().log(Status.PASS,
            "TC004: Job result cards validated successfully");
    }

    /**
     * TC005 - Click first job and verify detail panel
     */
    @Test(priority = 5, description = "TC005 - Click on the first job card",
          dependsOnMethods = "tc004_verifyJobResultCards")
    public void tc005_clickFirstJobCard() {
        ExtentReportManager.getTest().log(Status.INFO,
            "TC005: Clicking first job card to open details");

        boolean clicked = jobsPage.clickFirstJobCard();
        Assert.assertTrue(clicked, "Failed to click the first job card");

        ExtentReportManager.getTest().log(Status.PASS,
            "TC005: First job card clicked successfully");
    }

    // ─── API Tests ───────────────────────────────────────────────────────────

    /**
     * TC006 - Verify LinkedIn API endpoint is reachable
     */
    @Test(priority = 6, description = "TC006 - API: LinkedIn API endpoint reachability check")
    public void tc006_apiReachabilityCheck() {
        ExtentReportManager.getTest().log(Status.INFO,
            "TC006: Checking API reachability at " + ConfigReader.getApiBaseUrl());

        LinkedInApiClient apiClient = new LinkedInApiClient();
        boolean reachable = apiClient.isApiReachable();

        ExtentReportManager.getTest().log(Status.INFO,
            "API reachable: " + reachable);

        Assert.assertTrue(reachable,
            "LinkedIn API is not reachable at: " + ConfigReader.getApiBaseUrl());

        ExtentReportManager.getTest().log(Status.PASS,
            "TC006: API reachability confirmed");
    }

    /**
     * TC007 - Validate job search API returns HTTP 200
     */
    @Test(priority = 7, description = "TC007 - API: Job search returns 200 status")
    public void tc007_apiJobSearchReturns200() {
        ExtentReportManager.getTest().log(Status.INFO,
            "TC007: Calling job search API");

        LinkedInApiClient apiClient = new LinkedInApiClient();
        Response response = apiClient.searchJobs("Java Developer", "Bangalore", 0, 10);

        int statusCode = apiClient.getStatusCode(response);
        ExtentReportManager.getTest().log(Status.INFO,
            "API response status: " + statusCode);
        ExtentReportManager.getTest().log(Status.INFO,
            "Response time (ms): " + response.getTime());

        Assert.assertEquals(statusCode, 200,
            "Expected 200 from job search API but got: " + statusCode);

        ExtentReportManager.getTest().log(Status.PASS,
            "TC007: Job search API returned HTTP 200");
    }
}
