package com.carbcalculator;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CarbCalculatorTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "https://www.calculator.net/carbohydrate-calculator.html";

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cage")));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-01: Functional / Happy Path
     * Given Metric, Age=25, Male, 180cm, 60kg, Light
     * When calculate is pressed
     * Then results display carb intake breakdown
     */
    @Test
    void testValidInputs() {
        WebElement age = driver.findElement(By.id("cage"));
        age.clear();
        age.sendKeys("25");

        driver.findElement(By.id("csex1")).click();

        WebElement height = driver.findElement(By.id("cheightmeter"));
        height.clear();
        height.sendKeys("180");

        WebElement weight = driver.findElement(By.id("ckg"));
        weight.clear();
        weight.sendKeys("60");

        new Select(driver.findElement(By.id("cactivity"))).selectByValue("1.375");

        driver.findElement(By.name("x")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("h2result")));
        String page = driver.getPageSource();
        assertTrue(page.contains("Calories/day"), "Results should show calorie breakdown");
    }

    /**
     * TC-07: Negative / Error Handling
     * Given Age="abc", valid remaining fields
     * When calculate is pressed
     * Then no crash, page stays functional
     */
    @Test
    void testInvalidAgeText() {
        WebElement age = driver.findElement(By.id("cage"));
        age.clear();
        age.sendKeys("abc");

        WebElement height = driver.findElement(By.id("cheightmeter"));
        height.clear();
        height.sendKeys("180");

        WebElement weight = driver.findElement(By.id("ckg"));
        weight.clear();
        weight.sendKeys("60");

        driver.findElement(By.name("x")).click();

        String source = driver.getPageSource();
        assertTrue(driver.getTitle().contains("Carbohydrate"), "Page should not crash");
        assertTrue(source.contains("Please provide an age between 18 and 80"), "Should not expose stack trace");
    }

    /**
     * TC-16: Usability
     * Given all fields filled and results displayed
     * When Clear is clicked
     * Then all fields reset to default values
     */
    @Test
    void testClearButton() {
        WebElement age = driver.findElement(By.id("cage"));
        age.clear();
        age.sendKeys("30");

        WebElement height = driver.findElement(By.id("cheightmeter"));
        height.clear();
        height.sendKeys("190");

        WebElement weight = driver.findElement(By.id("ckg"));
        weight.clear();
        weight.sendKeys("85");

        new Select(driver.findElement(By.id("cactivity"))).selectByValue("1.725");

        // Submit first to get results
        driver.findElement(By.name("x")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("h2result")));

        // Click Clear
        driver.findElement(By.cssSelector("input[value='Clear']")).click();

        // Verify fields are reset
        String ageValue = driver.findElement(By.id("cage")).getAttribute("value");
        String heightValue = driver.findElement(By.id("cheightmeter")).getAttribute("value");
        String kgValue = driver.findElement(By.id("ckg")).getAttribute("value");

        assertTrue(ageValue.isEmpty());
        assertTrue(heightValue.isEmpty());
        assertTrue(kgValue.isEmpty());
    }
}