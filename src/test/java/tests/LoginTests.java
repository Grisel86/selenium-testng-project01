package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SuccessPage;

/**
 * LoginTests — positive and negative scenarios for the Login page.
 *
 * Structure:
 *   - Positive : valid login, success page state, logout flow
 *   - Negative : wrong credentials, empty fields, data-driven invalid logins
 */
public class LoginTests extends BaseTest {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final String VALID_USER = "student";
    private static final String VALID_PASS = "Password123";

    // ═════════════════════════════════════════════════════════════════════════
    // POSITIVE SCENARIOS
    // ═════════════════════════════════════════════════════════════════════════

    @Test(description = "TC-P-01: Valid credentials redirect to success page")
    public void validLoginRedirectsToSuccessPage() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(VALID_USER, VALID_PASS);

        SuccessPage successPage = new SuccessPage(driver);
        Assert.assertTrue(successPage.isOnSuccessPage(),
                "Should be on success page after valid login");
    }

    @Test(description = "TC-P-02: Success page heading contains 'Logged In'")
    public void successPageShowsCorrectHeading() {
        new LoginPage(driver).open().login(VALID_USER, VALID_PASS);

        SuccessPage successPage = new SuccessPage(driver);
        String heading = successPage.getHeadingText();
        Assert.assertTrue(
                heading.contains("Logged In") || heading.contains("Successfully"),
                "Heading should confirm login. Actual: " + heading);
    }

    @Test(description = "TC-P-03: Logout link is visible after valid login")
    public void logoutLinkVisibleAfterLogin() {
        new LoginPage(driver).open().login(VALID_USER, VALID_PASS);

        SuccessPage successPage = new SuccessPage(driver);
        Assert.assertTrue(successPage.isLogoutLinkDisplayed(),
                "Logout link should be visible on success page");
    }

    @Test(description = "TC-P-04: Clicking logout returns to login page")
    public void logoutReturnsToLoginPage() {
        new LoginPage(driver).open().login(VALID_USER, VALID_PASS);

        SuccessPage successPage = new SuccessPage(driver);
        LoginPage loginPage = successPage.clickLogout();

        Assert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username field should be visible after logout");
    }

    @Test(description = "TC-P-05: Login page has all expected form elements")
    public void loginPageElementsPresent() {
        LoginPage loginPage = new LoginPage(driver).open();

        Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), "Username field missing");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), "Password field missing");
        Assert.assertTrue(loginPage.isSubmitButtonDisplayed(),  "Submit button missing");
    }

    @Test(description = "TC-P-06: Login page title contains 'Test Login'")
    public void loginPageHasCorrectTitle() {
        LoginPage loginPage = new LoginPage(driver).open();

        Assert.assertTrue(loginPage.getPageTitle().contains("Test Login"),
                "Page title should contain 'Test Login'. Actual: " + loginPage.getPageTitle());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NEGATIVE SCENARIOS
    // ═════════════════════════════════════════════════════════════════════════

    @Test(description = "TC-N-01: Wrong username shows error message")
    public void wrongUsernameShowsError() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login("wronguser", VALID_PASS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should appear for wrong username");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Your username is invalid!"),
                "Error text mismatch. Actual: " + loginPage.getErrorMessage());
    }

    @Test(description = "TC-N-02: Wrong password shows error message")
    public void wrongPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(VALID_USER, "wrongpassword");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should appear for wrong password");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Your password is invalid!"),
                "Error text mismatch. Actual: " + loginPage.getErrorMessage());
    }

    @Test(description = "TC-N-03: Empty username field shows error")
    public void emptyUsernameShowsError() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login("", VALID_PASS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should appear when username is empty");
    }

    @Test(description = "TC-N-04: Empty password field shows error")
    public void emptyPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(VALID_USER, "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should appear when password is empty");
    }

    @Test(description = "TC-N-05: Both fields empty shows error")
    public void bothFieldsEmptyShowsError() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login("", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should appear when both fields are empty");
    }

    @Test(description = "TC-N-06: Page title does not match wrong expected value")
    public void pageTitleDoesNotMatchWrongValue() {
        LoginPage loginPage = new LoginPage(driver).open();

        Assert.assertFalse(loginPage.getPageTitle().contains("This Title Does Not Exist"),
                "Page title should NOT match a wrong expected value");
    }

    /**
     * TC-N-07: Data-driven invalid login combinations.
     * Each row = { username, password, expectedErrorFragment }
     */
    @Test(
            description  = "TC-N-07: Data-driven invalid credentials all show errors",
            dataProvider = "invalidCredentials"
    )
    public void invalidCredentialsShowError(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(username, password);

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError),
                String.format("Expected error containing '%s' but got '%s'", expectedError, actualError));
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
                { "wronguser",  VALID_PASS,       "Your username is invalid!" },
                { VALID_USER,   "wrongpass",       "Your password is invalid!" },
                { "admin",      "admin",           "Your username is invalid!" },
                { "",           VALID_PASS,        "Your username is invalid!" },
                { VALID_USER,   "",                "Your password is invalid!" },
                { "STUDENT",    VALID_PASS,        "Your username is invalid!" }, // case-sensitive check
        };
    }
}