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

public class HomePage {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    private By cityInput = By.xpath("//*[@id=\"c-omni-container\"]/div/div[1]/div/input");
    private By searchInput = By.xpath("//input[contains(@placeholder,'Search doctors')]");
    private By labTestsLink = By.xpath("//*[text()='Lab Tests']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
        this.actions = new Actions(driver);
    }

    public void searchLocation(String location) {
        WebElement city = wait.until(ExpectedConditions.elementToBeClickable(cityInput));

        city.sendKeys(Keys.CONTROL + "a");
        city.sendKeys(Keys.DELETE);
        city.sendKeys(location);

        By suggestion = By.xpath("//div[text()='" + location + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(suggestion));
    }

    public void searchService(String service) {
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchInput));

        search.clear();
        search.sendKeys(service);

        By suggestion = By.xpath("//*[text()='" + service + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();
        wait.until(ExpectedConditions.urlContains("search"));
    }

    public void clickLabTests() {
        wait.until(ExpectedConditions.elementToBeClickable(labTestsLink)).click();
    }

    public void navigateToCorporateWellness() {
        driver.navigate().to("https://www.practo.com/");

        By forCorporates = By.xpath(
                "//*[contains(@class,'nav-interact') and contains(text(),'For Corporates')]");
        By wellnessPlans = By.xpath("//a[@href='/plus/corporate']");

        WebElement corporatesMenu = wait.until(ExpectedConditions.elementToBeClickable(forCorporates));
        actions.moveToElement(corporatesMenu).perform();

        WebElement wellness = wait.until(ExpectedConditions.visibilityOfElementLocated(wellnessPlans));
        try {
            wellness.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", wellness);
        }
    }
}