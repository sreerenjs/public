package com.linkedin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * LinkedInJobsPage - Page Object for the LinkedIn Jobs Search page.
 * Locators are declared as named By.xpath(...) fields — mirrors the JS locator style.
 */
public class LinkedInJobsPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // ─── Locators ─────────────────────────────────────────────────────────────

    By JOB_TITLE_SEARCH_BOX      = By.xpath("//input[contains(@id,'jobs-search-box-keyword') or @aria-label='Search by title, skill, or company']");
    By LOCATION_SEARCH_BOX       = By.xpath("//input[contains(@id,'jobs-search-box-location') or @aria-label='City, state, or zip code']");
    By SEARCH_SUBMIT_BUTTON      = By.xpath("//button[contains(@class,'jobs-search-box__submit')] | //button[@type='submit' and ancestor::*[contains(@class,'jobs-search')]]");
    By JOB_CARDS                 = By.xpath("//ul[contains(@class,'jobs-search__results-list')]//li[contains(@class,'jobs-search-results__list-item')]");
    By JOB_CARD_TITLE            = By.xpath(".//a[contains(@class,'job-card-list__title') or contains(@class,'job-card-container__link')]");
    By JOB_CARD_COMPANY          = By.xpath(".//span[contains(@class,'job-card-container__primary-description')] | .//a[contains(@class,'job-card-container__company-name')]");
    By JOB_CARD_LOCATION         = By.xpath(".//li[contains(@class,'job-card-container__metadata-item')]");
    By EXPERIENCE_LEVEL_FILTER   = By.xpath("//button[.//span[normalize-space()='Experience Level']]");
    By FILTER_SHOW_RESULTS_BUTTON= By.xpath("//button[contains(@data-control-name,'filter_show_results')] | //button[normalize-space()='Show results']");
    By RESULT_COUNT              = By.xpath("//div[contains(@class,'jobs-search-results-list__subtitle')]//span");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public LinkedInJobsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    public LinkedInJobsPage searchJobs(String jobTitle, String location) {
        WebElement titleBox = wait.until(ExpectedConditions.elementToBeClickable(JOB_TITLE_SEARCH_BOX));
        titleBox.clear();
        titleBox.sendKeys(jobTitle);

        WebElement locationBox = driver.findElement(LOCATION_SEARCH_BOX);
        locationBox.clear();
        locationBox.sendKeys(location);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(SEARCH_SUBMIT_BUTTON)).click();
        } catch (Exception e) {
            locationBox.submit();
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(JOB_CARDS));
        return this;
    }

    public LinkedInJobsPage filterByExperienceLevel(String level) {
        wait.until(ExpectedConditions.elementToBeClickable(EXPERIENCE_LEVEL_FILTER)).click();

        // Dynamic locator built inline — still XPath, scoped to the level argument
        By EXPERIENCE_OPTION = By.xpath("//label[normalize-space()='" + level + "']");
        wait.until(ExpectedConditions.elementToBeClickable(EXPERIENCE_OPTION)).click();

        driver.findElement(FILTER_SHOW_RESULTS_BUTTON).click();
        return this;
    }

    public List<String> getJobTitles() {
        List<String> titles = new ArrayList<>();
        List<WebElement> cards = driver.findElements(JOB_CARDS);
        for (WebElement card : cards) {
            try {
                titles.add(card.findElement(JOB_CARD_TITLE).getText().trim());
            } catch (Exception e) {
                titles.add("N/A");
            }
        }
        return titles;
    }

    public List<String[]> getJobResults() {
        List<String[]> results = new ArrayList<>();
        List<WebElement> cards = driver.findElements(JOB_CARDS);
        for (WebElement card : cards) {
            String title    = getTextSafe(card, JOB_CARD_TITLE);
            String company  = getTextSafe(card, JOB_CARD_COMPANY);
            String location = getTextSafe(card, JOB_CARD_LOCATION);
            results.add(new String[]{title, company, location});
        }
        return results;
    }

    public String getResultCount() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_COUNT))
                       .getText().trim();
        } catch (Exception e) {
            return "0 results";
        }
    }

    public boolean clickFirstJobCard() {
        try {
            List<WebElement> cards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(JOB_CARDS)
            );
            if (!cards.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", cards.get(0)
                );
                cards.get(0).findElement(JOB_CARD_TITLE).click();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Could not click first job card: " + e.getMessage());
        }
        return false;
    }

    public boolean isOnJobsPage() {
        return driver.getCurrentUrl().contains("/jobs");
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private String getTextSafe(WebElement parent, By locator) {
        try {
            return parent.findElement(locator).getText().trim();
        } catch (Exception e) {
            return "N/A";
        }
    }
}
