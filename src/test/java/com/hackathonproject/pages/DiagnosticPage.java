package com.hackathonproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

// Page Object class representing Diagnostic page
public class DiagnosticPage {

    // WebDriver instance to interact with browser
    WebDriver driver;

    // Locator to identify city elements on the page (using CSS selector)
    private final By cityItems = By.cssSelector(".u-margint--standard");

    // Constructor to initialize driver
    public DiagnosticPage(WebDriver driver) {
        this.driver = driver;
    }

    // Method to fetch top cities displayed on the page
    public List<String> getTopCities() {

        // Create a list to store city names
        List<String> cities = new ArrayList<>();

        // Find all elements matching the city locator
        List<WebElement> elements = driver.findElements(cityItems);

        // Loop through each element
        for (WebElement el : elements) {
            try {
                // Get visible text of each element and add to list
                cities.add(el.getText());
            } catch (Exception e) {
                // Ignore exceptions like StaleElementReferenceException
                // (element may no longer be attached to DOM)
            }
        }

        // Return list of city names
        return cities;
    }
}