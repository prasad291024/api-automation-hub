package com.petstore.api.base;

import com.petstore.api.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

/**
 * Base Test Class
 *
 * This class serves as the foundation for all test classes.
 * It configures RestAssured settings, logging, and common specifications.
 * All test classes should extend this class to inherit the setup.
 *
 * @author Chintamani
 * @version 1.0
 */
public class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    /**
     * Setup method executed before all tests in the class
     * Configures RestAssured base settings and specifications
     */
    @BeforeClass
    public void setup() {
        logger.info("Setting up Base Test Configuration");

        // Set base URI for all requests
        RestAssured.baseURI = Config.BASE_URL;

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

        logger.info("Base Test Configuration completed successfully");
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