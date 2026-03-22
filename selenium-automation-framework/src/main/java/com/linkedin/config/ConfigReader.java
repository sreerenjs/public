package com.linkedin.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Reads values from config.properties
 * Single point of access for all configuration data
 */
public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    // Static block: loads properties once when class is first used
    static {
        try {
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            properties = new Properties();
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    // Private constructor — no instantiation needed (utility class)
    private ConfigReader() {}

    public static String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public static String getAppUrl() {
        return properties.getProperty("app.url");
    }

    public static String getUsername() {
        return properties.getProperty("username");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static String getExcelFilePath() {
        return properties.getProperty("excel.file.path");
    }

    public static String getExcelSheetName() {
        return properties.getProperty("excel.sheet.name");
    }

    public static String getReportPath() {
        return properties.getProperty("report.path");
    }

    public static String getReportTitle() {
        return properties.getProperty("report.title");
    }

    public static String getReportName() {
        return properties.getProperty("report.name");
    }

    public static String getApiBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    public static String getApiToken() {
        return properties.getProperty("api.token");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait", "10"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait", "20"));
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }
}
