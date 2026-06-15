package com.hackathonproject.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.hackathonproject.base.BaseTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Page Object class representing Hospital Listing Page
public class HospitalListingPage {

    // WebDriver instance for browser interaction
    WebDriver driver;

    // Logger instance to track execution flow
    private static final Logger log = LogManager.getLogger(HospitalListingPage.class);

    // Locator for hospital cards on listing page
    private final By hospitalCards = By.cssSelector(".c-estb-card");

    // Constructor to initialize driver
    public HospitalListingPage(WebDriver driver) {
        this.driver = driver;
    }

    // Method to fetch hospitals that match given conditions (rating + 24x7 + parking)
    public List<String> getHospitalsWithParking(double minRating) throws InterruptedException {

        // List to store qualified hospital names
        List<String> qualifiedHospitals = new ArrayList<>();

        // Get current browser window handle (main tab)
        String mainHandle = driver.getWindowHandle();

        // Scroll down the page to load hospital cards
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight/4);");

        // Wait for content to load (static wait)
        Thread.sleep(2000);

        // Get list of hospital card elements
        List<WebElement> cards = driver.findElements(hospitalCards);

        // Log number of hospitals found
        log.info("Processing {} hospitals...", cards.size());

        // Iterate through each hospital card
        for (WebElement card : cards) {
            try {

                // Get full text of the card
                String cardText = card.getText();

                // Check if hospital rating is above required value
                boolean isHighRated = checkRating(cardText, minRating);

                // Check if hospital is open 24x7
                boolean is24x7 = checkOpen247(cardText);

                // Process only if both conditions are satisfied
                if (isHighRated && is24x7) {

                    // Locate hospital name link inside card
                    WebElement nameLink = card.findElement(By.cssSelector(".line-1"));

                    // Get hospital name
                    String hospitalName = nameLink.getText();

                    // Click hospital to open detail page (new tab/window)
                    nameLink.click();

                    // Switch to newly opened tab
                    switchTab(mainHandle);

                    // Check if parking facility is available
                    if (checkParkingAvailable()) {

                        // Log and store hospital name
                        log.info("Found: {}", hospitalName);
                        qualifiedHospitals.add(hospitalName);
                    }

                    // Close detail tab and switch back to main tab
                    driver.close();
                    driver.switchTo().window(mainHandle);
                }

            } catch (Exception e) {

                // Handle any exception while processing a card
                log.warn("Skipping card due to error: {}", e.getMessage());

                // If extra tab is open, close it and return to main tab
                if (driver.getWindowHandles().size() > 1) {
                    driver.close();
                    driver.switchTo().window(mainHandle);
                }
            }
        }

        // Return list of qualified hospitals
        return qualifiedHospitals;
    }

    // ===================== HELPER METHODS ===================== //

    // Method to check if rating is greater than minimum rating
    private boolean checkRating(String text, double minRating) {

        // Split card text into words
        for (String word : text.split("[\\n\\s]+")) {

            // Match decimal numbers between 0.0 and 5.9 (ratings)
            if (word.matches("[0-5]\\.[0-9]")) {

                // Convert to double and compare
                if (Double.parseDouble(word) > minRating) return true;
            }
        }

        // Return false if no valid rating found
        return false;
    }

    // Method to check if hospital is open 24/7
    private boolean checkOpen247(String text) {

        // Convert text to lowercase for easier matching
        text = text.toLowerCase();

        // Check for different variations of "24/7"
        return text.contains("24/7") || text.contains("open 24") || text.contains("open 24x7");
    }

    // Method to switch from main tab to newly opened tab
    private void switchTab(String mainHandle) {

        // Get all window handles
        Set<String> handles = driver.getWindowHandles();

        // Loop through handles and switch to the new tab
        for (String h : handles) {
            if (!h.equals(mainHandle)) driver.switchTo().window(h);
        }
    }

    // Method to check if parking facility exists on detail page
    private boolean checkParkingAvailable() {
        try {

            // Click "Read more info" if present (to expand details)
            List<WebElement> readMore = driver.findElements(
                    By.xpath("//*[text()='Read more info']"));

            if (!readMore.isEmpty()) readMore.get(0).click();

            // Locate all amenities listed
            List<WebElement> amenities = driver.findElements(
                    By.xpath("//*[@data-qa-id='amenity_item']"));

            // Check if any amenity contains "Parking"
            for (WebElement am : amenities) {
                if (am.getText().contains("Parking")) return true;
            }

        } catch (Exception e) {

            // Return false if any error occurs
            return false;
        }

        // Return false if parking not found
        return false;
    }
}
