package com.hackathonproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class CorporateWellnessPage {
    WebDriver driver;

    // Locators
    private final By name = By.id("name");
    private final By orgName = By.id("organizationName");
    private final By contact = By.id("contactNumber");
    private final By email = By.id("officialEmailId");
    private final By orgSize = By.id("organizationSize");
    private final By interest = By.id("interestedIn");
    private final By submitBtn = By.xpath("//header//button[@type='submit']"); // Adjusted locator

    public CorporateWellnessPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillForm(String uName, String uOrg, String uPhone, String uEmail) {
        driver.findElement(name).sendKeys(uName);
        driver.findElement(orgName).sendKeys(uOrg);
        driver.findElement(contact).sendKeys(uPhone);
        driver.findElement(email).sendKeys(uEmail);

        new Select(driver.findElement(orgSize)).selectByVisibleText("<500");
        new Select(driver.findElement(interest)).selectByVisibleText("Taking a demo");
    }

    public boolean isSubmitEnabled() {
        return driver.findElement(submitBtn).isEnabled();
    }

    public void clickSubmit() {
        if(isSubmitEnabled()) {
            driver.findElement(submitBtn).click();
        }
    }
}