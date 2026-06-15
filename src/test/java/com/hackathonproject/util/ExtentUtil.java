package com.hackathonproject.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Utility class to manage Extent Reports
public class ExtentUtil {

    // Logger instance for logging report-related activities
    private static final Logger log = LogManager.getLogger(ExtentUtil.class);

    // Main ExtentReports object (handles entire report)
    private static ExtentReports extent;

    // ThreadLocal to store test instances (supports parallel execution)
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // Path where HTML report will be generated
    private static final String REPORT_PATH = "target/extent-report/ExtentReport.html";

    // Method to initialize Extent Report
    public static void initReport() {

        // Create Spark reporter (HTML report generator)
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

        // Configure report theme (DARK mode UI)
        spark.config().setTheme(Theme.DARK);

        // Set document title (browser tab name)
        spark.config().setDocumentTitle("Practo Automation Report");

        // Set report title shown in UI
        spark.config().setReportName("Hackathon Test Results");

        // Create main ExtentReports object
        extent = new ExtentReports();

        // Attach Spark reporter to ExtentReports
        extent.attachReporter(spark);

        // Add system/environment details in report
        extent.setSystemInfo("Project",  "Practo Hackathon");
        extent.setSystemInfo("Tester",   "Cognizant Team");
        extent.setSystemInfo("Browser",  "Chrome / Edge");
        extent.setSystemInfo("Environment", "Production");

        // Log report initialization
        log.info("Extent Report initialized: {}", REPORT_PATH);
    }

    // Method to create a new test entry in report
    public static ExtentTest createTest(String testName) {

        // Create test node with given name
        ExtentTest extentTest = extent.createTest(testName);

        // Store test in ThreadLocal for parallel execution
        test.set(extentTest);

        // Return test object
        return extentTest;
    }

    // Method to get current thread's test instance
    public static ExtentTest getTest() {

        // Return test associated with current thread
        return test.get();
    }

    // Method to flush (save) the report
    public static void flushReport() {

        // Ensure report object exists
        if (extent != null) {

            // Write all test data to HTML report file
            extent.flush();

            // Log successful report generation
            log.info("Extent Report saved: {}", REPORT_PATH);
        }
    }
}
