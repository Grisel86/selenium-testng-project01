package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for: https://practicetestautomation.com/logged-in-successfully/
 *
 * Returned by LoginPage after a successful login.
 */
public class SuccessPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public static final String URL = "https://practicetestautomation.com/logged-in-successfully/";

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading  = By.tagName("h1");
    private final By logoutLink   = By.partialLinkText("Log out");
    private final By successText  = By.cssSelector(".post-content p");

    // ── Constructor ───────────────────────────────────────────────────────────
    public SuccessPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ── Getters / State queries ───────────────────────────────────────────────
    public String getHeadingText() {
        try {
            WebElement h1 = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(pageHeading));
            return h1.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isLogoutLinkDisplayed() {
        try {
            WebElement link = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(logoutLink));
            return link.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnSuccessPage() {
        return driver.getCurrentUrl().contains("logged-in-successfully");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    public LoginPage clickLogout() {
        driver.findElement(logoutLink).click();
        return new LoginPage(driver);
    }
}