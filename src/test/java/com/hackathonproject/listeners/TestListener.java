package com.hackathonproject.listeners;

import com.aventstack.extentreports.Status;
import com.hackathonproject.base.BaseTest;
import com.hackathonproject.util.ExtentUtil;
import com.hackathonproject.util.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.IOException;

// TestNG Listener class to track test execution events
public class TestListener implements ITestListener {

    // Logger instance for logging test execution details
    private static final Logger log = LogManager.getLogger(TestListener.class);

    // Runs before the test suite starts
    @Override
    public void onStart(ITestContext context) {
        log.info("========== TEST SUITE STARTED: {} ==========", context.getName());

        // Initialize Extent Report
        ExtentUtil.initReport();           // <-- ADDED
    }

    // Runs after the test suite finishes
    @Override
    public void onFinish(ITestContext context) {
        log.info("========== TEST SUITE FINISHED: {} ==========", context.getName());

        // Log summary of test results
        log.info("Passed : {}", context.getPassedTests().size());
        log.info("Failed : {}", context.getFailedTests().size());
        log.info("Skipped: {}", context.getSkippedTests().size());

        // Finalize and save Extent Report
        ExtentUtil.flushReport();
    }

    // Runs when individual test starts
    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> TEST STARTED: {}", result.getName());

        // Create test entry in Extent Report
        ExtentUtil.createTest(result.getName());   // <-- ADDED
    }

    // Runs when test passes
    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔ TEST PASSED: {}", result.getName());

        // Log PASS status in Extent Report
        ExtentUtil.getTest().log(Status.PASS, "Test passed");   // <-- ADDED
    }

    // Runs when test fails
    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘ TEST FAILED: {}", result.getName());

        // Log failure reason
        log.error("Reason: {}", result.getThrowable().getMessage());

        // Add failure details to Extent Report
        ExtentUtil.getTest().log(Status.FAIL, result.getThrowable());   // <-- ADDED

        try {
            // Take screenshot if driver is available
            if (BaseTest.getDriver() != null) {

                // Save screenshot with test name
                ScreenshotUtil.takeScreenshot(BaseTest.getDriver(), "FAILED_" + result.getName());
            }
        } catch (IOException e) {

            // Log error if screenshot capture fails
            log.error("Could not take failure screenshot: {}", e.getMessage());
        }
    }

    // Runs when test is skipped
    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠ TEST SKIPPED: {}", result.getName());

        // Log SKIP status in Extent Report
        ExtentUtil.getTest().log(Status.SKIP, "Test skipped");   // <-- ADDED
    }
}