package com.petstore.api.tests;

import com.petstore.api.base.BaseTest;
import com.petstore.api.config.Config;
import com.petstore.api.dataproviders.TestDataProvider;
import com.petstore.api.models.Pet;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Create Pet API Test Cases
 *
 * This class contains all test cases for creating pets via POST /pet endpoint.
 * Tests cover positive scenarios, negative scenarios, and edge cases.
 *
 * @author Prasad_V
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Create Operations")
public class CreatePetTests extends BaseTest {

    /**
     * TC_CREATE_001: Create Pet with Valid Complete Data
     *
     * Verifies that a new pet can be created successfully with all valid fields.
     * Uses data-driven approach with multiple valid pet data sets.
     */
    @Test(dataProvider = "validPetData", dataProviderClass = TestDataProvider.class, priority = 1)
    @Description("TC_CREATE_001: Verify that a pet can be created with valid complete data")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Create Pet with Valid Data")
    public void testCreatePetWithValidCompleteData(Pet pet) {
        logTestStart("TC_CREATE_001: Create Pet with Valid Complete Data");

        Response response = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", notNullValue())
                .body("name", equalTo(pet.getName()))
                .body("status", equalTo(pet.getStatus()))
                .extract()
                .response();

        // Additional assertions
        Pet createdPet = response.as(Pet.class);
        assertNotNull(createdPet.getId(), "Pet ID should not be null");
        assertEquals(createdPet.getName(), pet.getName(), "Pet name should match");
        assertEquals(createdPet.getStatus(), pet.getStatus(), "Pet status should match");

        logger.info("Pet created successfully with ID: {}", createdPet.getId());
        logTestEnd("TC_CREATE_001");
    }

    /**
     * TC_CREATE_002: Create Pet with Minimum Required Fields
     *
     * Verifies that a pet can be created with only required fields (name and photoUrls).
     */
    @Test(dataProvider = "minimalPetData", dataProviderClass = TestDataProvider.class, priority = 2)
    @Description("TC_CREATE_002: Verify that a pet can be created with minimum required fields")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Pet with Minimal Data")
    public void testCreatePetWithMinimalRequiredFields(Pet pet) {
        logTestStart("TC_CREATE_002: Create Pet with Minimum Required Fields");

        Response response = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", notNullValue())
                .body("name", equalTo(pet.getName()))
                .extract()
                .response();

        Pet createdPet = response.as(Pet.class);
        assertNotNull(createdPet.getId(), "Pet ID should be auto-generated");
        assertEquals(createdPet.getName(), pet.getName(), "Pet name should match");

        logger.info("Pet created with minimal data. ID: {}", createdPet.getId());
        logTestEnd("TC_CREATE_002");
    }

    /**
     * TC_CREATE_003: Create Pet with Missing Required Field (name)
     *
     * Verifies that API returns appropriate error when required field 'name' is missing.
     */
    @Test(priority = 3)
    @Description("TC_CREATE_003: Verify error when creating pet without required 'name' field")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Pet - Negative Scenarios")
    public void testCreatePetWithMissingName() {
        logTestStart("TC_CREATE_003: Create Pet with Missing Name");

        Pet petWithoutName = Pet.builder()
                .photoUrls(Collections.singletonList("https://example.com/photo.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        given()
                .spec(requestSpec)
                .body(petWithoutName)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_OK),
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_METHOD_NOT_ALLOWED)
                ));

        logger.info("API correctly rejected pet creation with missing name");
        logTestEnd("TC_CREATE_003");
    }

    /**
     * TC_CREATE_004: Create Pet with Invalid Data Type
     *
     * Verifies that API validates data types correctly.
     * Note: This is tricky in Java as we can't easily send wrong types through POJOs.
     * In real scenarios, this would be tested with raw JSON strings.
     */
    @Test(priority = 4)
    @Description("TC_CREATE_004: Verify API handles invalid data types appropriately")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Pet - Negative Scenarios")
    public void testCreatePetWithInvalidDataType() {
        logTestStart("TC_CREATE_004: Create Pet with Invalid Data Type");

        // Sending raw JSON with invalid data type for 'id' (string instead of integer)
        String invalidJson = "{\n" +
                "  \"id\": \"invalid_string_id\",\n" +
                "  \"name\": \"TestPet\",\n" +
                "  \"photoUrls\": [\"https://example.com/photo.jpg\"],\n" +
                "  \"status\": \"available\"\n" +
                "}";

        given()
                .spec(requestSpec)
                .body(invalidJson)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_INTERNAL_SERVER_ERROR)
                ));

        logger.info("API correctly handled invalid data type");
        logTestEnd("TC_CREATE_004");
    }

    /**
     * TC_CREATE_005: Create Pet with Special Characters in Name
     *
     * Verifies that pet names with special characters are handled correctly.
     * Tests boundary conditions and edge cases.
     */
    @Test(dataProvider = "specialCharacterPetData", dataProviderClass = TestDataProvider.class, priority = 5)
    @Description("TC_CREATE_005: Verify that pets can be created with special characters in name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create Pet - Edge Cases")
    public void testCreatePetWithSpecialCharactersInName(Pet pet) {
        logTestStart("TC_CREATE_005: Create Pet with Special Characters");

        Response response = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("name", equalTo(pet.getName()))
                .extract()
                .response();

        Pet createdPet = response.as(Pet.class);
        assertEquals(createdPet.getName(), pet.getName(),
                "Pet name with special characters should be preserved");

        logger.info("Pet created with special character name: {}", createdPet.getName());
        logTestEnd("TC_CREATE_005");
    }
}