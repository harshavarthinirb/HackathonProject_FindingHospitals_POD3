package com.hackathonproject.base;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.hackathonproject.util.ConfigReader;

import java.io.File;
import java.time.Duration;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // ThreadLocal: each parallel thread gets its own driver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Keep the public static accessor so all existing step/page classes compile unchanged
    public static WebDriver driver() {
        return driverThreadLocal.get();
    }

    // Backward-compatible field-style access via a getter used in pages/steps
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    private boolean isCI() {
        return "true".equalsIgnoreCase(System.getenv("CI"))
                || "ci".equalsIgnoreCase(System.getProperty("env"));
    }

    //    private void setupDriver(String property, String localPath, Runnable wdmSetup) {
//        if (!isCI() && localPath != null && new File(localPath).exists()) {
//            System.setProperty(property, localPath);
//            log.info("Using local driver: " + localPath);
//        } else {
//            log.info("Using WebDriverManager for: " + property);
//            wdmSetup.run();
//        }
//    }
    private void setupDriver(String property, String localPath, Runnable wdmSetup) {
        if (localPath != null && new File(localPath).exists()) {
            System.setProperty(property, localPath);
            log.info("Using local driver: " + localPath);
        } else {
            log.info("Local driver not found, falling back to WebDriverManager");
            wdmSetup.run();
        }
    }

    @Before
    public void setUp() {
        // Each thread checks its OWN driver, not a shared static one
        if (driverThreadLocal.get() != null) return;

        String browser = com.hackathonproject.runner.TestRunner.getBrowserName() != null
                ? com.hackathonproject.runner.TestRunner.getBrowserName()
                : ConfigReader.get("browser");

        log.info("Initializing Browser: " + browser);

        WebDriver driver;

        if (browser.equalsIgnoreCase("chrome")) {
            setupDriver("webdriver.chrome.driver",
                    ConfigReader.get("chrome.driver.path"),
                    () -> WebDriverManager.chromedriver().setup());
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("edge")) {
            setupDriver("webdriver.edge.driver",
                    ConfigReader.get("edge.driver.path"),
                    () -> WebDriverManager.edgedriver().setup());
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            driver = new EdgeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            setupDriver("webdriver.gecko.driver",
                    ConfigReader.get("firefox.driver.path"),
                    () -> WebDriverManager.firefoxdriver().setup());
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--start-maximized");
            driver = new FirefoxDriver(options);

        } else {
            log.error("Browser not supported: " + browser);
            throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        driverThreadLocal.set(driver);

        driver.get(ConfigReader.get("base.url"));
        log.info("Navigated to Practo home page");
    }

    @After
    public void tearDown() {
        // keep browser open between scenarios
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            log.info("Closing browser");
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}