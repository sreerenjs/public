package com.linkedin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * LinkedInHomePage - Page Object for LinkedIn Home/Feed page.
 * Locators are declared as named By.xpath(...) fields — mirrors the JS locator style.
 */
public class LinkedInHomePage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // ─── Locators ─────────────────────────────────────────────────────────────

    By JOBS_NAV_LINK      = By.xpath("//nav[@aria-label='Primary']//a[contains(@href,'/jobs')] | //a[contains(@href,'jobs') and .//*[local-name()='svg' and @data-test-icon='jobs-medium']]");
    By PROFILE_ICON       = By.xpath("//button[contains(@class,'global-nav__me-photo') or @aria-label='Me']");
    By GLOBAL_SEARCH_BAR  = By.xpath("//input[contains(@class,'search-global-typeahead__input') or @aria-label='Search']");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public LinkedInHomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(PROFILE_ICON));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LinkedInJobsPage navigateToJobs() {
        wait.until(ExpectedConditions.elementToBeClickable(JOBS_NAV_LINK)).click();
        return new LinkedInJobsPage(driver, wait);
    }

    public LinkedInJobsPage searchForJobsViaGlobalSearch(String keyword) {
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(GLOBAL_SEARCH_BAR));
        searchBar.clear();
        searchBar.sendKeys(keyword);
        searchBar.submit();
        return new LinkedInJobsPage(driver, wait);
    }
}
