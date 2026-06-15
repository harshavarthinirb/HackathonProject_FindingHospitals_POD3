package com.hackathonproject.hooks;

// Import required classes
import com.hackathonproject.base.BaseTest;
import com.hackathonproject.util.ExtentUtil;
import com.hackathonproject.util.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CucumberHooks {

    // Logger to print execution details in console/log files
    private static final Logger log = LogManager.getLogger(CucumberHooks.class);

    // ===================== GLOBAL SETUP ===================== //

    // Runs once before all test scenarios start
    @BeforeAll
    public static void beforeAll() {
        log.info("========== TEST EXECUTION STARTED ==========");

        // Initialize Extent Report (HTML report generation)
        ExtentUtil.initReport();
    }

    // ===================== SCENARIO SETUP ===================== //

    // Runs before each scenario
    @Before
    public void beforeScenario(Scenario scenario) {

        // Log scenario name
        log.info("---------- SCENARIO STARTED: {} ----------", scenario.getName());

        // Log scenario tags (useful for filtering execution later)
        log.info("Tags: {}", scenario.getSourceTagNames());
    }

    // ===================== SCENARIO TEARDOWN ===================== //

    // Runs after each scenario
    @After
    public void afterScenario(Scenario scenario) {

        // Log scenario result (PASSED / FAILED / SKIPPED)
        log.info("Scenario Status: {}", scenario.getStatus());

        // Check if scenario failed
        if (scenario.isFailed()) {
            log.error("SCENARIO FAILED: {}", scenario.getName());

            try {
                // Check if driver exists before taking screenshot
                if (BaseTest.getDriver() != null) {

                    // Capture screenshot as byte array
                    byte[] screenshot = ScreenshotUtil.takeScreenshotAsBytes(BaseTest.getDriver());

                    // Attach screenshot to Cucumber report
                    scenario.attach(screenshot, "image/png", "Failed_" + scenario.getName());

                    log.info("Failure screenshot attached to report");
                }
            } catch (Exception e) {

                // Handle any error during screenshot capture
                log.error("Could not attach screenshot: {}", e.getMessage());
            }
        }

        // Log end of scenario
        log.info("---------- SCENARIO ENDED: {} ----------", scenario.getName());
    }

    // ===================== GLOBAL TEARDOWN ===================== //

    // Runs once after all scenarios are completed
    @AfterAll
    public static void afterAll() {
        log.info("========== TEST EXECUTION COMPLETED ==========");

        // Flush (write and finalize) the Extent Report
        ExtentUtil.flushReport();
    }
}