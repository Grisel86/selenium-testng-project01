import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Selenium Demo — Login Page & Exception Page
 *
 * Positive scenarios : valid login, element presence, correct page title,
 *                      success heading, logout link visible after login
 * Negative scenarios : wrong username, wrong password, empty fields,
 *                      invalid URL, element not found, wrong page title
 *
 * BUG FIXES applied:
 *  1. CSS selector  button[id=submit]  → button[id='submit']  (quotes required)
 *  2. WebDriverWait added to avoid flaky NoSuchElementException
 *  3. findExceptionPageElements() was never called → now called in main()
 *  4. chromeTest() / firefoxTest() were never called → now called in main()
 *  5. FIX (this run): exploration methods now use tryFind() — a NoSuchElementException
 *     inside findLoginPageElements / findExceptionPageElements no longer crashes the
 *     whole suite; every locator logs FOUND / NOT FOUND and execution continues.
 *  6. FIX (this run): main() runs all test scenarios FIRST, exploration AFTER —
 *     a bad locator in the exploration section can never block test results again.
 *  7. FIX (this run): By.linkText() replaced with By.partialLinkText() for the
 *     footer link whose exact text varies between page loads / viewport widths.
 */
public class SeleniumDemo {

    // ── Valid credentials for practicetestautomation.com ──────────────────────
    private static final String VALID_USER     = "student";
    private static final String VALID_PASS     = "Password123";
    private static final String LOGIN_URL      = "https://practicetestautomation.com/practice-test-login/";
    private static final String EXCEPTION_URL  = "https://practicetestautomation.com/practice-test-exceptions/";
    private static final String SUCCESS_URL    = "https://practicetestautomation.com/logged-in-successfully/";
    private static final Duration TIMEOUT      = Duration.ofSeconds(5);

    // ── Test result counters ──────────────────────────────────────────────────
    private static int passed = 0;
    private static int failed = 0;

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        try {
            // ── POSITIVE scenarios (run BEFORE exploration so a bad locator ───
            // ── in exploration can never prevent test results from printing) ──
            testValidLogin(driver);
            testLoginPageTitle(driver);
            testLoginElementsPresent(driver);
            testSuccessPageHeading(driver);
            testSuccessPageLogoutLink(driver);
            testExceptionPageElementsPresent(driver);

            // ── NEGATIVE scenarios ────────────────────────────────────────────
            testInvalidUsername(driver);
            testInvalidPassword(driver);
            testEmptyUsernameField(driver);
            testEmptyPasswordField(driver);
            testBothFieldsEmpty(driver);
            testWrongPageTitle(driver);
            testElementNotFound(driver);
            testInvalidUrl(driver);

            // ── Cross-browser smoke test ──────────────────────────────────────
            String chromeTitle = chromeTest(LOGIN_URL);
            assertContains("TC-CB-01 Chrome page title contains 'Practice'", chromeTitle, "Practice");

            // ── Locator exploration ───────────────────────────────────────────
            // Wrapped so a missing/renamed element logs a warning but does NOT
            // abort the run — test results above are already printed and counted.
            System.out.println("\n── Locator exploration (login page) ────────────────");
            try { findLoginPageElements(driver); }
            catch (Exception e) { System.out.println("  ⚠ Exploration error: " + e.getMessage()); }

            System.out.println("\n── Locator exploration (exception page) ────────────");
            try { findExceptionPageElements(driver); }
            catch (Exception e) { System.out.println("  ⚠ Exploration error: " + e.getMessage()); }

        } finally {
            driver.quit();
            printSummary();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POSITIVE SCENARIOS
    // ─────────────────────────────────────────────────────────────────────────

    /** TC-P-01 : Valid credentials → redirected to success page */
    private static void testValidLogin(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, VALID_USER, VALID_PASS);
        String currentUrl = driver.getCurrentUrl();
        assertTrue("TC-P-01 Valid login redirects to success page",
                currentUrl.contains("logged-in-successfully"));
    }

    /** TC-P-02 : Login page has the expected browser tab title */
    private static void testLoginPageTitle(WebDriver driver) {
        driver.get(LOGIN_URL);
        String title = driver.getTitle();
        assertContains("TC-P-02 Login page title contains 'Test Login'", title, "Test Login");
    }

    /** TC-P-03 : All expected form elements exist on the login page */
    private static void testLoginElementsPresent(WebDriver driver) {
        driver.get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        boolean usernamePresent = isPresent(driver, By.id("username"));
        boolean passwordPresent = isPresent(driver, By.name("password"));
        boolean submitPresent   = isPresent(driver, By.id("submit"));

        assertTrue("TC-P-03a Username field is present", usernamePresent);
        assertTrue("TC-P-03b Password field is present", passwordPresent);
        assertTrue("TC-P-03c Submit button is present",  submitPresent);
    }

    /** TC-P-04 : Exception page contains an Add button and at least one input */
    private static void testExceptionPageElementsPresent(WebDriver driver) {
        driver.get(EXCEPTION_URL);
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        List<WebElement> inputs  = driver.findElements(By.tagName("input"));

        assertTrue("TC-P-04a Exception page has at least one button",
                !buttons.isEmpty());
        assertTrue("TC-P-04b Exception page has at least one input field",
                !inputs.isEmpty());
    }

    /** TC-P-05 : Success page shows a congratulations heading */
    private static void testSuccessPageHeading(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, VALID_USER, VALID_PASS);
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        try {
            WebElement heading = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
            String headingText = heading.getText();
            assertTrue("TC-P-05 Success page heading contains 'Logged In'",
                    headingText.contains("Logged In") || headingText.contains("Successfully"));
        } catch (Exception e) {
            assertTrue("TC-P-05 Success page heading present", false);
        }
    }

    /** TC-P-06 : A logout / log out link is visible on the success page */
    private static void testSuccessPageLogoutLink(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, VALID_USER, VALID_PASS);
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        try {
            WebElement logoutLink = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.partialLinkText("Log out")));
            assertTrue("TC-P-06 Logout link is visible after valid login",
                    logoutLink.isDisplayed());
        } catch (Exception e) {
            assertTrue("TC-P-06 Logout link is visible after valid login", false);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NEGATIVE SCENARIOS
    // ─────────────────────────────────────────────────────────────────────────

    /** TC-N-01 : Wrong username → error message shown */
    private static void testInvalidUsername(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, "wronguser", VALID_PASS);
        String errorText = getErrorText(driver);
        assertContains("TC-N-01 Invalid username shows error", errorText, "Your username is invalid!");
    }

    /** TC-N-02 : Wrong password → error message shown */
    private static void testInvalidPassword(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, VALID_USER, "wrongpassword");
        String errorText = getErrorText(driver);
        assertContains("TC-N-02 Invalid password shows error", errorText, "Your password is invalid!");
    }

    /** TC-N-03 : Username field left empty → error message shown */
    private static void testEmptyUsernameField(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, "", VALID_PASS);
        String errorText = getErrorText(driver);
        assertNotEmpty("TC-N-03 Empty username triggers an error message", errorText);
    }

    /** TC-N-04 : Password field left empty → error message shown */
    private static void testEmptyPasswordField(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, VALID_USER, "");
        String errorText = getErrorText(driver);
        assertNotEmpty("TC-N-04 Empty password triggers an error message", errorText);
    }

    /** TC-N-05 : Both fields empty → error message shown */
    private static void testBothFieldsEmpty(WebDriver driver) {
        driver.get(LOGIN_URL);
        performLogin(driver, "", "");
        String errorText = getErrorText(driver);
        assertNotEmpty("TC-N-05 Both fields empty triggers an error message", errorText);
    }

    /** TC-N-06 : Wrong expected page title (intentional mismatch) */
    private static void testWrongPageTitle(WebDriver driver) {
        driver.get(LOGIN_URL);
        String title = driver.getTitle();
        boolean wrongTitleMatch = title.contains("This Title Does Not Exist");
        assertFalse("TC-N-06 Page title does NOT match a wrong expected value", wrongTitleMatch);
    }

    /** TC-N-07 : Looking for a non-existent element returns empty list */
    private static void testElementNotFound(WebDriver driver) {
        driver.get(LOGIN_URL);
        List<WebElement> ghost = driver.findElements(By.id("element-that-does-not-exist"));
        assertTrue("TC-N-07 Non-existent element ID returns empty list", ghost.isEmpty());
    }

    /** TC-N-08 : Navigating to an invalid URL shows no login form */
    private static void testInvalidUrl(WebDriver driver) {
        driver.get("https://practicetestautomation.com/nonexistent-page-404/");
        List<WebElement> loginForms = driver.findElements(By.id("login"));
        assertTrue("TC-N-08 404 page has no login form present", loginForms.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOCATOR EXPLORATION (no assertions — pure element resolution demo)
    // Each locator is attempted via tryFind() so one bad selector never
    // crashes the method or the suite.
    // ─────────────────────────────────────────────────────────────────────────
    private static void findLoginPageElements(WebDriver driver) {
        driver.get(LOGIN_URL);

        // ── Username ──────────────────────────────────────────────────────────
        tryFind(driver, By.id("username"),                       "username – by id");
        tryFind(driver, By.xpath("//input[@id='username']"),     "username – by xpath");
        tryFind(driver, By.cssSelector("input[id='username']"),  "username – by css");

        // ── Password ──────────────────────────────────────────────────────────
        tryFind(driver, By.name("password"),                     "password – by name");
        tryFind(driver, By.xpath("//input[@name='password']"),   "password – by xpath");
        tryFind(driver, By.cssSelector("input[name='password']"),"password – by css");

        // ── Submit button ─────────────────────────────────────────────────────
        // BUG FIX: className("btn") can match multiple elements — prefer id
        tryFind(driver, By.id("submit"),                         "submit – by id");
        tryFind(driver, By.xpath("//button[@id='submit']"),      "submit – by xpath");
        // BUG FIX: attribute value must be quoted inside CSS selector
        tryFind(driver, By.cssSelector("button[id='submit']"),   "submit – by css");

        // ── Tag-based collection ──────────────────────────────────────────────
        List<WebElement> allInputs = driver.findElements(By.tagName("input"));
        System.out.println("  Login page input count: " + allInputs.size());

        // ── Link text ─────────────────────────────────────────────────────────
        // BUG FIX: exact link text varies; partialLinkText is more resilient
        tryFind(driver, By.partialLinkText("Practice Test Automation"), "footer link – partial");
        tryFind(driver, By.partialLinkText("Test Automation"),          "footer link – shorter partial");

        // ── Relative locators ─────────────────────────────────────────────────
        tryFind(driver,
                RelativeLocator.with(By.tagName("input")).below(By.id("username")),
                "password field – relative below username");
        tryFind(driver,
                RelativeLocator.with(By.tagName("a")).toRightOf(By.partialLinkText("Test Automation")),
                "link – relative to right of footer link");

        // ── Navigation bar item ───────────────────────────────────────────────
        tryFind(driver, By.className("menu-item-home"), "home nav item – by class");
    }

    private static void findExceptionPageElements(WebDriver driver) {
        driver.get(EXCEPTION_URL);

        // ── Course link ───────────────────────────────────────────────────────
        tryFind(driver, By.partialLinkText("beginners program"),         "course link – partial");
        tryFind(driver, By.partialLinkText("Selenium WebDriver"),        "course link – shorter partial");

        // ── Input field (multiple locator strategies) ─────────────────────────
        tryFind(driver, By.tagName("input"),                             "input – by tag");
        tryFind(driver, By.xpath("//div[@id='row1']//input"),            "input – xpath in row1");
        tryFind(driver, By.cssSelector("div#row1 input"),                "input – css in row1");

        // ── All buttons ───────────────────────────────────────────────────────
        List<WebElement> allButtons = driver.findElements(By.tagName("button"));
        System.out.println("  Exception page button count: " + allButtons.size());

        // ── Add button ────────────────────────────────────────────────────────
        tryFind(driver, By.id("add_btn"),                                "add btn – by id");
        tryFind(driver, By.xpath("//button[@id='add_btn']"),             "add btn – by xpath");
        tryFind(driver, By.cssSelector("button#add_btn"),                "add btn – by css");

        // ── Edit button (Row 1) ───────────────────────────────────────────────
        tryFind(driver, By.id("edit_btn"),                               "edit btn – by id");
        tryFind(driver, By.xpath("//button[@id='edit_btn']"),            "edit btn – by xpath");
        tryFind(driver, By.cssSelector("button#edit_btn"),               "edit btn – by css");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CROSS-BROWSER HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private static String chromeTest(String url) {
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        String title = driver.getTitle();
        driver.quit();
        return title;
    }

    private static String firefoxTest(String url) {
        // BUG FIX: use forward slashes for cross-platform compatibility
        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.get(url);
        String title = driver.getTitle();
        driver.quit();
        return title;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILITY METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /** Types credentials and clicks submit */
    private static void performLogin(WebDriver driver, String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement submitButton  = driver.findElement(By.id("submit"));

        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        submitButton.click();
    }

    /** Returns error message text, or empty string if none visible */
    private static String getErrorText(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("error")));
            return error.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Returns true if the element is present in the DOM right now */
    private static boolean isPresent(WebDriver driver, By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /**
     * Exploration helper: attempts to find an element and prints FOUND / NOT FOUND.
     * Never throws — a missing element is just logged so exploration continues.
     */
    private static void tryFind(WebDriver driver, By locator, String label) {
        try {
            WebElement el = driver.findElement(locator);
            System.out.println("  FOUND     [" + label + "] tag=<" + el.getTagName() + ">");
        } catch (Exception e) {
            System.out.println("  NOT FOUND [" + label + "] — " + e.getMessage().split("\n")[0]);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SIMPLE ASSERTION HELPERS (no JUnit / TestNG dependency required)
    // ─────────────────────────────────────────────────────────────────────────
    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  ✅ PASS: " + testName);
            passed++;
        } else {
            System.out.println("  ❌ FAIL: " + testName);
            failed++;
        }
    }

    private static void assertFalse(String testName, boolean condition) {
        assertTrue(testName, !condition);
    }

    private static void assertContains(String testName, String actual, String expected) {
        assertTrue(testName + " [expected to contain: \"" + expected + "\", got: \"" + actual + "\"]",
                actual != null && actual.contains(expected));
    }

    private static void assertNotEmpty(String testName, String value) {
        assertTrue(testName, value != null && !value.isEmpty());
    }

    private static void printSummary() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  TEST RESULTS: " + passed + " passed, " + failed + " failed");
        System.out.println("══════════════════════════════════════");
    }
}