package com.linkedin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * LinkedInLoginPage - Page Object for the LinkedIn Login screen.
 * Locators are declared as named By.xpath(...) fields — mirrors the JS locator style.
 */
public class LinkedInLoginPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // ─── Locators ─────────────────────────────────────────────────────────────

    By EMAIL_FIELD    = By.xpath("//input[@id='username' or @name='session_key']");
    By PASSWORD_FIELD = By.xpath("//input[@id='password' or @name='session_password']");
    By SIGN_IN_BUTTON = By.xpath("//button[@type='submit' and contains(@class,'sign-in-form__submit')]");
    By SIGN_IN_BTN_ALT= By.xpath("//button[normalize-space()='Sign in']");
    By ERROR_MESSAGE  = By.xpath("//div[contains(@class,'alert-content')]//p");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public LinkedInLoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    public LinkedInLoginPage enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD));
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public LinkedInLoginPage enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD));
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public LinkedInHomePage clickSignIn() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(SIGN_IN_BUTTON)).click();
        } catch (Exception e) {
            driver.findElement(SIGN_IN_BTN_ALT).click();
        }
        return new LinkedInHomePage(driver, wait);
    }

    /** Convenience: full login in one call */
    public LinkedInHomePage login(String email, String password) {
        return enterEmail(email)
               .enterPassword(password)
               .clickSignIn();
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE))
                       .getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
