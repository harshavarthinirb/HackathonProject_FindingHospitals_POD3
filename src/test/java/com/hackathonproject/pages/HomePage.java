package com.hackathonproject.pages;


import com.hackathonproject.util.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

// Page Object class representing Home Page
public class HomePage {

    // WebDriver instance to control browser
    WebDriver driver;

    // Explicit wait for handling dynamic elements
    WebDriverWait wait;

    // Actions class for mouse/keyboard interactions (like hover)
    Actions actions;


    private By cityInput = By.xpath("//*[@id=\"c-omni-container\"]/div/div[1]/div/input");
    private By searchInput = By.xpath("//input[contains(@placeholder,'Search doctors')]");

    private By labTestsLink = By.xpath("//*[text()='Lab Tests']");

    // ===================== CONSTRUCTOR ===================== //

    // Constructor to initialize driver, wait, and actions
    public HomePage(WebDriver driver) {
        this.driver = driver;

        // Initialize explicit wait using value from config file
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));

        // Initialize Actions class for advanced interactions
        this.actions = new Actions(driver);
    }

    // ===================== ACTION METHODS ===================== //

    // Method to search and select a location (city)
    public void searchLocation(String location) {

        // Wait until city input is clickable
        WebElement city = wait.until(ExpectedConditions.elementToBeClickable(cityInput));

        // Clear existing text (CTRL + A then DELETE)
        city.sendKeys(Keys.CONTROL + "a");
        city.sendKeys(Keys.DELETE);
        city.sendKeys(location);

        // Locator for matching suggestion in dropdown
        By suggestion = By.xpath("//div[text()='" + location + "']");

        // Wait for suggestion to appear and click it
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();

        // Wait until suggestion disappears (ensures selection is complete)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(suggestion));
    }

    // Method to search for a service or doctor
    public void searchService(String service) {

        // Wait until search box is clickable
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchInput));

        search.clear();
        search.sendKeys(service);
        By suggestion = By.xpath("//*[text()='" + service + "']");

        // Wait for suggestion and click
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();

        // Wait until navigated to search results page (URL contains "search")
        wait.until(ExpectedConditions.urlContains("search"));
    }

    // Method to navigate to Lab Tests page
    public void clickLabTests() {

        // Wait until Lab Tests link is clickable and click it
        wait.until(ExpectedConditions.elementToBeClickable(labTestsLink)).click();
    }

    // Method to navigate to Corporate Wellness section
    public void navigateToCorporateWellness() {

        // Navigate to homepage explicitly
        driver.navigate().to("https://www.practo.com/");

        // Locator for "For Corporates" menu
        By forCorporates = By.xpath(
                "//*[contains(@class,'nav-interact') and contains(text(),'For Corporates')]");

        // Locator for "Wellness Plans" submenu/link
        By wellnessPlans = By.xpath("//a[@href='/plus/corporate']");

        // Wait for corporates menu and hover over it
        WebElement corporatesMenu = wait.until(ExpectedConditions.elementToBeClickable(forCorporates));
        actions.moveToElement(corporatesMenu).perform();

        // Wait until wellness plans option is visible
        WebElement wellness = wait.until(ExpectedConditions.visibilityOfElementLocated(wellnessPlans));

        try {
            // Try normal click
            wellness.click();
        } catch (Exception e) {

            // If normal click fails, use JavaScript click as fallback
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", wellness);
        }
    }
}