package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ExceptionPage;

/**
 * ExceptionTests — scenarios for the Practice Test Exceptions page.
 *
 * Covers: element visibility, interactability, dynamic content after actions.
 */
public class ExceptionTests extends BaseTest {

    // ═════════════════════════════════════════════════════════════════════════
    // POSITIVE SCENARIOS
    // ═════════════════════════════════════════════════════════════════════════

    @Test(description = "TC-EX-P-01: Add button is present on page load")
    public void addButtonIsPresentOnLoad() {
        ExceptionPage page = new ExceptionPage(driver).open();

        Assert.assertTrue(page.isAddButtonDisplayed(),
                "Add button should be visible on page load");
    }

    @Test(description = "TC-EX-P-02: Clicking Add reveals Row 2 input")
    public void clickingAddRevealsRow2() {
        ExceptionPage page = new ExceptionPage(driver).open();
        page.clickAdd();

        Assert.assertTrue(page.isRow2Displayed(),
                "Row 2 input should appear after clicking Add");
    }

    @Test(description = "TC-EX-P-03: Clicking Edit enables Row 1 input field")
    public void clickingEditEnablesRow1Input() {
        ExceptionPage page = new ExceptionPage(driver).open();
        page.clickEdit();

        Assert.assertTrue(page.isRow1InputEnabled(),
                "Row 1 input should be enabled after clicking Edit");
    }

    @Test(description = "TC-EX-P-04: Page has at least two buttons")
    public void pageHasMultipleButtons() {
        ExceptionPage page = new ExceptionPage(driver).open();

        Assert.assertTrue(page.getButtonCount() >= 2,
                "Exception page should have at least 2 buttons. Found: " + page.getButtonCount());
    }

    @Test(description = "TC-EX-P-05: Typing in Row 2 and saving shows confirmation")
    public void typingInRow2AndSavingShowsConfirmation() {
        ExceptionPage page = new ExceptionPage(driver).open();
        page.clickAdd();
        page.typeInRow2("Gri");
        page.clickSave();

        String confirmation = page.getConfirmationText();
        Assert.assertFalse(confirmation.isEmpty(),
                "Confirmation message should appear after saving Row 2");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NEGATIVE SCENARIOS
    // ═════════════════════════════════════════════════════════════════════════

    @Test(description = "TC-EX-N-01: Row 2 is NOT visible before clicking Add")
    public void row2NotVisibleBeforeClickingAdd() {
        ExceptionPage page = new ExceptionPage(driver).open();

        Assert.assertFalse(page.isRow2Displayed(),
                "Row 2 should NOT be visible before Add is clicked");
    }
}