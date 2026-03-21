package com.prasad_v.allure;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AllureMetadataWriter {
    private static final CustomLogger logger = new CustomLogger(AllureMetadataWriter.class);
    private static final String DEFAULT_RESULTS_DIR = "allure-results";

    private AllureMetadataWriter() {
    }

    public static void initialize(String suiteName) {
        Path resultsDir = Paths.get(System.getProperty("allure.results.directory", DEFAULT_RESULTS_DIR));
        try {
            Files.createDirectories(resultsDir);
            writeEnvironment(resultsDir);
            writeCategories(resultsDir);
            writeExecutor(resultsDir, suiteName);
        } catch (IOException e) {
            logger.warn("Unable to initialize Allure metadata files: " + e.getMessage());
        }
    }

    private static void writeEnvironment(Path resultsDir) throws IOException {
        String env = valueOrDefault(System.getenv("ENV"), "Local");
        String browser = valueOrDefault(System.getenv("BROWSER"), "API");
        String appVersion = valueOrDefault(System.getenv("APP_VERSION"), "1.0-SNAPSHOT");
        String baseUrl = resolveBaseUrl();
        String content = String.join("\n",
                "Browser=" + browser,
                "Environment=" + env,
                "Java.Version=" + valueOrDefault(System.getProperty("java.version"), "unknown"),
                "OS=" + valueOrDefault(System.getProperty("os.name"), "unknown"),
                "App.Version=" + appVersion,
                "BaseURL=" + baseUrl
        ) + "\n";
        Files.writeString(resultsDir.resolve("environment.properties"), content, StandardCharsets.UTF_8);
    }

    private static void writeCategories(Path resultsDir) throws IOException {
        String content = """
                [
                  { "name": "Ignored tests", "matchedStatuses": ["skipped"] },
                  {
                    "name": "Infrastructure bugs",
                    "matchedStatuses": ["broken"],
                    "messageRegex": ".*(Connection refused|Timeout|NoSuchElement).*"
                  },
                  { "name": "Product defects", "matchedStatuses": ["failed"] },
                  { "name": "Test defects", "matchedStatuses": ["broken"] }
                ]
                """;
        Files.writeString(resultsDir.resolve("categories.json"), content, StandardCharsets.UTF_8);
    }

    private static void writeExecutor(Path resultsDir, String suiteName) throws IOException {
        String buildNumber = valueOrDefault(
                System.getenv("GITHUB_RUN_NUMBER"),
                valueOrDefault(System.getenv("BUILD_NUMBER"), "local")
        );

        String name = "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS")) ? "GitHub Actions" : "Local Run";
        String type = "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS")) ? "github" : "local";
        String buildName = "Run #" + buildNumber;
        String reportName = valueOrDefault(suiteName, "Allure Report");

        String content = String.format(
                "{ \"name\": \"%s\", \"type\": \"%s\", \"buildName\": \"%s\", \"reportName\": \"%s\" }%n",
                escapeJson(name), escapeJson(type), escapeJson(buildName), escapeJson(reportName)
        );
        Files.writeString(resultsDir.resolve("executor.json"), content, StandardCharsets.UTF_8);
    }

    private static String resolveBaseUrl() {
        String fromEnv = valueOrDefault(System.getenv("BASE_URL"), "");
        if (!fromEnv.isBlank()) {
            return fromEnv;
        }
        String fromConfig = ConfigurationManager.getInstance().getProperty("api.base.url", "");
        return fromConfig.isBlank() ? "N/A" : fromConfig;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
