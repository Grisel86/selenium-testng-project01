package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for: https://practicetestautomation.com/practice-test-login/
 *
 * All locators and interactions with the Login page live here.
 * Tests never call driver.findElement() directly — they call methods on this class.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── URL ───────────────────────────────────────────────────────────────────
    public static final String URL = "https://practicetestautomation.com/practice-test-login/";

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By usernameField = By.id("username");
    private final By passwordField = By.name("password");
    private final By submitButton  = By.id("submit");
    private final By errorMessage  = By.id("error");

    // ── Constructor ───────────────────────────────────────────────────────────
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    public LoginPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        return this;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    public LoginPage enterUsername(String username) {
        WebElement field = driver.findElement(usernameField);
        field.clear();
        field.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement field = driver.findElement(passwordField);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public void clickSubmit() {
        driver.findElement(submitButton).click();
    }

    /** Convenience method: fill both fields and submit in one call */
    public void login(String username, String password) {
        enterUsername(username)
                .enterPassword(password)
                .clickSubmit();
    }

    // ── Getters / State queries ───────────────────────────────────────────────
    public String getErrorMessage() {
        try {
            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return error.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorDisplayed() {
        return !getErrorMessage().isEmpty();
    }

    public boolean isUsernameFieldDisplayed() {
        return !driver.findElements(usernameField).isEmpty();
    }

    public boolean isPasswordFieldDisplayed() {
        return !driver.findElements(passwordField).isEmpty();
    }

    public boolean isSubmitButtonDisplayed() {
        return !driver.findElements(submitButton).isEmpty();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}