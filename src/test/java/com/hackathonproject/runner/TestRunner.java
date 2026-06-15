package com.hackathonproject.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import com.hackathonproject.base.BaseTest;
import com.hackathonproject.util.ExcelWriter;

// Cucumber configuration for test execution
@CucumberOptions(
        // Path to feature files
        features = "src/test/resources/features",

        // Step definitions + Hooks + Base classes location
        glue = {"com.hackathonproject.steps", "com.hackathonproject.base", "com.hackathonproject.hooks"},

        // Tag filter → only scenarios tagged with @Smoke will run
        tags = "@Smoke",   // ← Runs all @Smoke scenarios across all groups

        // Reporting plugins configuration
        plugin = {
                "pretty", // Console output in readable format
                "html:target/cucumber-reports/cucumber.html", // HTML report
                "json:target/cucumber-reports/cucumber.json", // JSON report
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:", // Extent report
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // Allure report
        }
)
public class TestRunner extends AbstractTestNGCucumberTests {

    // ThreadLocal variable to store browser type per thread (supports parallel runs)
    private static final ThreadLocal<String> browserName = new ThreadLocal<>();

    // Getter method to fetch browser name
    public static String getBrowserName() {
        return browserName.get();
    }

    // Runs before test class execution
    @BeforeClass

    // Accepts browser parameter from TestNG XML or CLI
    @Parameters("browser")
    public void setBrowser(@Optional("chrome") String browser) {

        // Store browser name in ThreadLocal
        browserName.set(browser);
    }

    // Runs after all tests in class are completed
    @AfterClass
    public void closeBrowser() {

        // Cleanup any Excel resources (file writers, streams)
        ExcelWriter.cleanup();

        // Quit WebDriver (close browser)
        BaseTest.quitDriver();

        // Clear ThreadLocal to avoid memory leaks
        browserName.remove();
    }
}