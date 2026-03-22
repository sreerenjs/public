package com.linkedin.base;

import com.aventstack.extentreports.Status;
import com.linkedin.config.ConfigReader;
import com.linkedin.utils.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * BaseTest - Parent class for all test classes
 *
 * Responsibilities:
 * - Initialises / tears down WebDriver
 * - Inits / flushes Extent Reports
 * - Captures screenshots on failure
 * - Exposes driver + explicit wait to child tests
 */
public class BaseTest {

    // ThreadLocal: each parallel thread gets its own WebDriver instance
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected WebDriverWait wait;

    // ─── Suite-level hooks ────────────────────────────────────────────────────

    @BeforeSuite
    public void beforeSuite() {
        ExtentReportManager.initReport();
    }

    @AfterSuite
    public void afterSuite() {
        ExtentReportManager.flushReport();
    }

    // ─── Test-level hooks ─────────────────────────────────────────────────────

    @BeforeMethod
    public void setUp(ITestResult result) {
        // Create ExtentTest node using the test method name
        ExtentReportManager.createTest(result.getMethod().getMethodName());

        initDriver();
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(ConfigReader.getExplicitWait()));

        getDriver().manage().window().maximize();
        getDriver().manage().timeouts()
                   .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));

        ExtentReportManager.getTest().log(Status.INFO, "Browser launched: " + ConfigReader.getBrowser());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Log PASS / FAIL / SKIP and capture screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            ExtentReportManager.getTest().log(Status.FAIL, "Test FAILED: " + result.getThrowable());
            captureScreenshot(result.getMethod().getMethodName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportManager.getTest().log(Status.SKIP, "Test SKIPPED: " + result.getThrowable());
        } else {
            ExtentReportManager.getTest().log(Status.PASS, "Test PASSED");
        }

        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }

    // ─── Driver accessor ─────────────────────────────────────────────────────

    public static WebDriver getDriver() {
        return driver.get();
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private void initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (ConfigReader.isHeadless()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--window-size=1920,1080"
                );
                driver.set(new ChromeDriver(options));
                break;
        }
    }

    private void captureScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) getDriver();
            byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);

            Path screenshotDir = Paths.get("reports/screenshots");
            Files.createDirectories(screenshotDir);

            String fileName = testName + "_" + System.currentTimeMillis() + ".png";
            Path screenshotPath = screenshotDir.resolve(fileName);
            Files.write(screenshotPath, screenshotBytes);

            // Embed screenshot into the Extent Report
            ExtentReportManager.getTest()
                .addScreenCaptureFromPath(screenshotPath.toString(), "Failure Screenshot");

            ExtentReportManager.getTest().log(Status.INFO, "Screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            ExtentReportManager.getTest().log(Status.WARNING,
                "Could not capture screenshot: " + e.getMessage());
        }
    }
}
