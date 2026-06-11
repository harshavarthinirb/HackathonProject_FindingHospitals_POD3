package com.hackathonproject.base;

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

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private static final ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();

    // ✅ Single driver getter
    public static WebDriver getDriver() {
        return threadDriver.get();
    }

    // ===================== SETUP ===================== //

    @Before
    public void setUp() {

        if (threadDriver.get() != null) return;

        String browser = com.hackathonproject.runner.TestRunner.getBrowserName() != null
                ? com.hackathonproject.runner.TestRunner.getBrowserName()
                : ConfigReader.get("browser");

        log.info("===== Opening browser: " + browser + " =====");

        WebDriver driver = switch (browser.toLowerCase()) {
            case "edge" -> new EdgeDriver(buildEdgeOptions());
            case "firefox" -> new FirefoxDriver(buildFirefoxOptions());
            default -> new ChromeDriver(buildChromeOptions());
        };

        threadDriver.set(driver);

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        driver.get(ConfigReader.get("base.url"));
        log.info("Navigated to: " + ConfigReader.get("base.url"));
    }

    // ===================== TEARDOWN ===================== //

    @After
    public void tearDown() {
        // keeping browser open intentionally
    }

    public static void quitDriver() {
        WebDriver driver = threadDriver.get();
        if (driver != null) {
            log.info("===== Closing browser =====");
            driver.quit();
            threadDriver.remove();
        }
    }

    // ===================== OPTIONS ===================== //

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280,800");
        options.addArguments("--window-position=0,0");
        applyCommonArguments(options);
        return options;
    }

    private EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--window-size=1280,800");
        applyCommonArguments(options);
        return options;
    }

    private FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1280");
        options.addArguments("--height=800");
        return options;
    }

    // ✅ Common options for Chrome & Edge
    private void applyCommonArguments(org.openqa.selenium.chromium.ChromiumOptions<?> options) {
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
    }
}