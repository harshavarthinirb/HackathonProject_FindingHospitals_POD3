package com.hackathonproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

// Page Object class representing Corporate Wellness page
public class CorporateWellnessPage {

    // WebDriver instance to interact with browser
    WebDriver driver;


    private final By name = By.id("name");
    private final By orgName = By.id("organizationName");
    private final By contact = By.id("contactNumber");
    private final By email = By.id("officialEmailId");
    private final By orgSize = By.id("organizationSize");
    private final By interest = By.id("interestedIn");

    // Submit button (located using XPath)
    private final By submitBtn = By.xpath("//header//button[@type='submit']"); // Adjusted locator

    // Constructor to initialize driver when object is created
    public CorporateWellnessPage(WebDriver driver) {
        this.driver = driver;
    }

    // ===================== ACTION METHODS ===================== //

    // Method to fill the form fields with user data
    public void fillForm(String uName, String uOrg, String uPhone, String uEmail) {


        driver.findElement(name).sendKeys(uName);
        driver.findElement(orgName).sendKeys(uOrg);
        driver.findElement(contact).sendKeys(uPhone);
        driver.findElement(email).sendKeys(uEmail);

        // Select organization size from dropdown
        new Select(driver.findElement(orgSize)).selectByVisibleText("<500");

        // Select interest from dropdown
        new Select(driver.findElement(interest)).selectByVisibleText("Taking a demo");
    }

    // Method to check if submit button is enabled
    public boolean isSubmitEnabled() {
        return driver.findElement(submitBtn).isEnabled();
    }

    // Method to click submit button (only if it's enabled)
    public void clickSubmit() {

        // Check if button is enabled before clicking
        if(isSubmitEnabled()) {

            // Click submit button
            driver.findElement(submitBtn).click();
        }
    }
}