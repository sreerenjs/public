package com.linkedin.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.linkedin.config.ConfigReader;

/**
 * ExtentReportManager - Manages the lifecycle of ExtentReports
 *
 * Usage pattern:
 *   ExtentReportManager.initReport();         // once in @BeforeSuite
 *   ExtentReportManager.createTest("TC001");  // once per test
 *   ExtentReportManager.getTest().pass(".."); // log steps inside tests
 *   ExtentReportManager.flushReport();        // once in @AfterSuite
 */
public class ExtentReportManager {

    private static ExtentReports extent;

    // ThreadLocal ensures each parallel test thread has its own ExtentTest node
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Initializes the reporter — call once in @BeforeSuite
     */
    public static void initReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(ConfigReader.getReportPath());
        sparkReporter.config().setDocumentTitle(ConfigReader.getReportTitle());
        sparkReporter.config().setReportName(ConfigReader.getReportName());
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", ConfigReader.getBrowser());
        extent.setSystemInfo("Environment", "QA");
    }

    /**
     * Creates a new test node in the report — call in @BeforeMethod
     */
    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    /**
     * Returns the current test's ExtentTest node for logging
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Writes all test results to the HTML file — call in @AfterSuite
     */
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
