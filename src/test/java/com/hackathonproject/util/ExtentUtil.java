package com.hackathonproject.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtentUtil {

    private static final Logger log = LogManager.getLogger(ExtentUtil.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    private static final String REPORT_PATH = "target/extent-report/ExtentReport.html";

    public static void initReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Practo Automation Report");
        spark.config().setReportName("Hackathon Test Results");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Project",  "Practo Hackathon");
        extent.setSystemInfo("Tester",   "Cognizant Team");
        extent.setSystemInfo("Browser",  "Chrome / Edge");
        extent.setSystemInfo("Environment", "Production");

        log.info("Extent Report initialized: {}", REPORT_PATH);
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            log.info("Extent Report saved: {}", REPORT_PATH);
        }
    }
}