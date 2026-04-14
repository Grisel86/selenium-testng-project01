package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for:<a href="https://practicetestautomation.com/practice-test-exceptions">...</a>...</a>
 */
public class ExceptionPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public static final String URL = "https://practicetestautomation.com/practice-test-exceptions/";

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By addButton        = By.id("add_btn");
    private final By editButton       = By.id("edit_btn");
    private final By saveButton       = By.id("save_btn");
    private final By row1Input        = By.xpath("//div[@id='row1']//input");
    private final By row2Input        = By.xpath("//div[@id='row2']//input");
    private final By confirmationText = By.id("confirmation");

    // ── Constructor ───────────────────────────────────────────────────────────
    public ExceptionPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    public ExceptionPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(addButton));
        return this;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    public ExceptionPage clickAdd() {
        driver.findElement(addButton).click();
        return this;
    }

    public ExceptionPage clickEdit() {
        driver.findElement(editButton).click();
        return this;
    }

    public ExceptionPage typeInRow1(String text) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(row1Input));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    // Remove the premature save button wait — just type and return
    public ExceptionPage typeInRow2(String text) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(row2Input));
        input.clear();
        input.sendKeys(text);
        return this;
    }

    public ExceptionPage clickSave() {
        // save_btn only appears after clicking Edit, not after Add
        wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton));
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        driver.findElement(saveButton).click();
        return this;
    }

    // ── Getters / State queries ───────────────────────────────────────────────
    public boolean isAddButtonDisplayed() {
        return !driver.findElements(addButton).isEmpty();
    }

    public boolean isRow2Displayed() {
        try {
            WebElement row2 = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(row2Input));
            return row2.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRow1InputEnabled() {
        try {
            WebElement input = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(row1Input));
            return input.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public String getConfirmationText() {
        try {
            WebElement confirm = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(confirmationText));
            return confirm.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getButtonCount() {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        return buttons.size();
    }
}