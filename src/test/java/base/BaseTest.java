package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

<<<<<<< HEAD
public class BaseTest {

    // FIX: initialize to null so compiler knows the variable is always declared
    protected WebDriver driver = null;
=======
/**
 * BaseTest — shared setup and teardown for all test classes.
 *
 * Every test class extends this. Driver lifecycle (open / quit)
 * is handled here so test classes stay focused on assertions only.
 */
public class BaseTest {

    protected WebDriver driver;
>>>>>>> 914554a2d20366e8195f7ec2e684f7e5f0a9f127

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
<<<<<<< HEAD
=======
        // options.addArguments("--headless");  // uncomment for CI runs
>>>>>>> 914554a2d20366e8195f7ec2e684f7e5f0a9f127
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
<<<<<<< HEAD
            driver = null; // clean up reference after each test
=======
>>>>>>> 914554a2d20366e8195f7ec2e684f7e5f0a9f127
        }
    }
}