package com.hackathonproject.util;

import com.hackathonproject.runner.TestRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWriter {
    private static final Logger log = LogManager.getLogger(ExcelWriter.class);

    // Each browser gets its own workbook instance via ThreadLocal
    private static final ThreadLocal<Workbook> workbook = ThreadLocal.withInitial(XSSFWorkbook::new);
    private static final ThreadLocal<Sheet> hospitalSheet = ThreadLocal.withInitial(() -> {
        Sheet sheet = workbook.get().createSheet("Hospitals");
        sheet.createRow(0).createCell(0).setCellValue("Hospital Name");
        return sheet;
    });
    private static final ThreadLocal<Sheet> citiesSheet = ThreadLocal.withInitial(() -> {
        Sheet sheet = workbook.get().createSheet("Top Cities");
        sheet.createRow(0).createCell(0).setCellValue("City Name");
        return sheet;
    });

    // File path includes browser name — e.g., target/generatedData_chrome.xlsx
    private static String getFilePath() {
        String browser = TestRunner.getBrowserName() != null ? TestRunner.getBrowserName() : "default";
        return "target/generatedData_" + browser + ".xlsx";
    }

    public static void writeHospitals(List<String> hospitals) {
        Sheet sheet = hospitalSheet.get();
        int rowNum = sheet.getLastRowNum() + 1;
        for (String hospital : hospitals) {
            sheet.createRow(rowNum++).createCell(0).setCellValue(hospital);
        }
        log.info("Written " + hospitals.size() + " hospitals to Excel");
        save();
    }

    public static void writeCities(List<String> cities) {
        Sheet sheet = citiesSheet.get();
        int rowNum = sheet.getLastRowNum() + 1;
        for (String city : cities) {
            sheet.createRow(rowNum++).createCell(0).setCellValue(city);
        }
        log.info("Written " + cities.size() + " cities to Excel");
        save();
    }

    private static void save() {
        try (FileOutputStream fos = new FileOutputStream(getFilePath())) {
            workbook.get().write(fos);
            log.info("Saved Excel: " + getFilePath());
        } catch (IOException e) {
            log.error("Failed to save Excel file: " + e.getMessage());
        }
    }

    // Call this in @AfterClass to clean up ThreadLocal
    public static void cleanup() {
        try {
            workbook.get().close();
        } catch (IOException e) {
            log.error("Failed to close workbook: " + e.getMessage());
        }
        workbook.remove();
        hospitalSheet.remove();
        citiesSheet.remove();
    }
}