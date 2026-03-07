package com.petstore.api.base;

import com.prasad_v.config.ConfigurationManager;
import com.petstore.api.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

/**
 * Base Test Class
 *
 * This class serves as the foundation for all test classes.
 * It configures RestAssured settings, logging, and common specifications.
 * All test classes should extend this class to inherit the setup.
 *
 * Integrated with framework-core's ConfigurationManager for environment-aware
 * config loading. Falls back to Config.BASE_URL if no properties file is found.
 *
 * @author Prasad_V
 * @version 2.0
 */
public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    /**
     * Setup method executed before all tests in the class
     * Configures RestAssured base settings and specifications.
     * Uses framework-core's ConfigurationManager to load base URL from
     * config/petstore.properties if available, otherwise falls back to Config.BASE_URL.
     */
    @BeforeClass
    public void setup() {
        logger.info("Setting up Petstore Base Test Configuration");

        // Load base URL from properties file if available, else fall back to Config constant
        String baseUrl = loadBaseUrl();
        RestAssured.baseURI = baseUrl;
        logger.info("Base URI set to: {}", baseUrl);

        // Configure default request specification
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured()) // Adds request/response to Allure report
                .log(LogDetail.ALL) // Log all request details
                .build();

        // Configure default response specification
        responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL) // Log all response details
                .build();

        logger.info("Petstore Base Test Configuration completed successfully");
    }

    /**
     * Loads the base URL using framework-core's ConfigurationManager.
     * Attempts to read 'base.url' from config/petstore.properties on the classpath.
     * If the file is missing or the property is not set, falls back to Config.BASE_URL.
     *
     * @return base URL string to be used for all API requests
     */
    private String loadBaseUrl() {
        try {
            ConfigurationManager config = ConfigurationManager.getInstance();
            config.loadConfigFromResource("config/petstore.properties");
            String url = config.getProperty("base.url");
            if (url != null && !url.isEmpty()) {
                return url;
            }
        } catch (Exception e) {
            logger.warn("Could not load petstore.properties, using default Config.BASE_URL: {}", e.getMessage());
        }
        return Config.BASE_URL;
    }

    /**
     * Helper method to get configured request specification
     *
     * @return RequestSpecification with all configurations
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .spec(requestSpec);
    }

    /**
     * Helper method to log test start
     *
     * @param testName Name of the test being executed
     */
    protected void logTestStart(String testName) {
        logger.info("========================================");
        logger.info("Starting Test: {}", testName);
        logger.info("========================================");
    }

    /**
     * Helper method to log test end
     *
     * @param testName Name of the test that was executed
     */
    protected void logTestEnd(String testName) {
        logger.info("========================================");
        logger.info("Test Completed: {}", testName);
        logger.info("========================================");
    }
}