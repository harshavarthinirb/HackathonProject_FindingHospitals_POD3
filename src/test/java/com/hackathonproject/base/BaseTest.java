package com.hackathonproject.base;

// Importing utility classes and required libraries
import com.hackathonproject.util.ConfigReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class BaseTest {

    // Logger for logging test execution details
    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // ThreadLocal to support parallel execution (each thread gets its own driver)
    private static final ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();

    //Method to get current thread's WebDriver instance
    public static WebDriver getDriver() {
        return threadDriver.get(); // returns driver for current thread
    }

    // ===================== SETUP ===================== //

    // Runs before each scenario
    @Before
    public void setUp() {

        // Prevent creating multiple drivers for same thread
        if (threadDriver.get() != null) return;

        // Get browser name from TestRunner or fallback to config file
        String browser = com.hackathonproject.runner.TestRunner.getBrowserName() != null
                ? com.hackathonproject.runner.TestRunner.getBrowserName()
                : ConfigReader.get("browser");

        log.info("===== Opening browser: " + browser + " =====");

        // Create WebDriver based on browser type
        WebDriver driver = switch (browser.toLowerCase()) {
            case "edge" -> new EdgeDriver(buildEdgeOptions());       // Edge browser
            case "firefox" -> new FirefoxDriver(buildFirefoxOptions()); // Firefox browser
            default -> new ChromeDriver(buildChromeOptions());      // Default = Chrome
        };

        // Store driver in ThreadLocal
        threadDriver.set(driver);

        // Set implicit wait (wait for elements globally)
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));

        // Set page load timeout
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        // Open application base URL
        driver.get(ConfigReader.get("base.url"));
        log.info("Navigated to: " + ConfigReader.get("base.url"));
    }

    // ===================== TEARDOWN ===================== //

    // Runs after each scenario
    @After
    public void tearDown() {
        // Browser is intentionally kept open (for debugging purpose)
    }

    // Custom method to quit driver manually when needed
    public static void quitDriver() {
        WebDriver driver = threadDriver.get();
        if (driver != null) {
            log.info("===== Closing browser =====");
            driver.quit();          // Close browser
            threadDriver.remove(); // Remove driver from thread
        }
    }

    // ===================== OPTIONS ===================== //

    // Chrome browser configuration
    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Set browser window size and position
        options.addArguments("--window-size=1280,800");
        options.addArguments("--window-position=0,0");

        // Apply common settings (shared with Edge)
        applyCommonArguments(options);
        return options;
    }

    // Edge browser configuration
    private EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();

        // Set browser window size
        options.addArguments("--window-size=1280,800");

        // Apply common settings
        applyCommonArguments(options);
        return options;
    }

    // Firefox browser configuration
    private FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();

        // Firefox uses different arguments for window size
        options.addArguments("--width=1280");
        options.addArguments("--height=800");
        return options;
    }

    // Common options for Chromium browsers (Chrome & Edge)
    private void applyCommonArguments(org.openqa.selenium.chromium.ChromiumOptions<?> options) {

        // Disable notifications popups
        options.addArguments("--disable-notifications");

        // Disable unwanted popup blocking interruptions
        options.addArguments("--disable-popup-blocking");
    }
}