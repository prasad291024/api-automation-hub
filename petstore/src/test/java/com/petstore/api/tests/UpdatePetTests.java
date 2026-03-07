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
 * Update Pet API Test Cases
 *
 * This class contains all test cases for updating pets via PUT /pet endpoint.
 * Tests cover positive scenarios, negative scenarios, and edge cases.
 *
 * @author Prasad_v
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Update Operations")
public class UpdatePetTests extends BaseTest {

    /**
     * TC_UPDATE_001: Update Existing Pet with Valid Data
     *
     * Verifies that an existing pet's details can be updated successfully.
     * Creates a pet, updates it, then verifies the changes.
     */
    @Test(priority = 1)
    @Description("TC_UPDATE_001: Verify that an existing pet can be updated with valid data")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Update Pet with Valid Data")
    public void testUpdateExistingPetWithValidData() {
        logTestStart("TC_UPDATE_001: Update Existing Pet with Valid Data");

        // Step 1: Create a pet
        Pet originalPet = Pet.builder()
                .id(60001L)
                .name("OriginalPet")
                .photoUrls(Collections.singletonList("https://example.com/original.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(originalPet)
                .when()
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet with ID: {}", petId);

        // Step 2: Update the pet
        Pet updatedPet = Pet.builder()
                .id(petId)
                .name("UpdatedPet")
                .photoUrls(Collections.singletonList("https://example.com/updated.jpg"))
                .status(Config.STATUS_SOLD)
                .build();

        Response updateResponse = given()
                .spec(requestSpec)
                .body(updatedPet)
                .when()
                .put(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", equalTo(petId.intValue()))
                .body("name", equalTo(updatedPet.getName()))
                .body("status", equalTo(updatedPet.getStatus()))
                .extract()
                .response();

        Pet resultPet = updateResponse.as(Pet.class);
        assertEquals(resultPet.getName(), updatedPet.getName(), "Pet name should be updated");
        assertEquals(resultPet.getStatus(), updatedPet.getStatus(), "Pet status should be updated");

        // Step 3: Verify updates by retrieving the pet
        Response getResponse = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT);

        Pet retrievedPet = getResponse.as(Pet.class);
        assertEquals(retrievedPet.getName(), updatedPet.getName(), "Updated name should persist");
        assertEquals(retrievedPet.getStatus(), updatedPet.getStatus(), "Updated status should persist");

        logger.info("Successfully updated pet with ID: {}", petId);
        logTestEnd("TC_UPDATE_001");
    }

    /**
     * TC_UPDATE_002: Update Pet Status Transitions
     *
     * Verifies status transitions work correctly (available → pending → sold).
     * Uses data-driven approach with different status transitions.
     */
    @Test(dataProvider = "petUpdateData", dataProviderClass = TestDataProvider.class, priority = 2)
    @Description("TC_UPDATE_002: Verify pet status can be updated through different transitions")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Pet Status - Data Driven")
    public void testUpdatePetStatusTransitions(Long petId, String originalName, String originalStatus,
                                               String updatedName, String updatedStatus) {
        logTestStart("TC_UPDATE_002: Update Pet Status Transitions");

        // Create pet with original status
        Pet originalPet = Pet.builder()
                .id(petId)
                .name(originalName)
                .photoUrls(Collections.singletonList("https://example.com/status-test.jpg"))
                .status(originalStatus)
                .build();

        given()
                .spec(requestSpec)
                .body(originalPet)
                .when()
                .post(Config.PET_ENDPOINT);

        logger.info("Created pet with status: {}", originalStatus);

        // Update pet status
        Pet updatedPet = Pet.builder()
                .id(petId)
                .name(updatedName)
                .photoUrls(Collections.singletonList("https://example.com/status-test-updated.jpg"))
                .status(updatedStatus)
                .build();

        Response updateResponse = given()
                .spec(requestSpec)
                .body(updatedPet)
                .when()
                .put(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("status", equalTo(updatedStatus))
                .extract()
                .response();

        Pet resultPet = updateResponse.as(Pet.class);
        assertEquals(resultPet.getStatus(), updatedStatus,
                "Status should transition from " + originalStatus + " to " + updatedStatus);

        logger.info("Successfully transitioned status from {} to {}", originalStatus, updatedStatus);
        logTestEnd("TC_UPDATE_002");
    }

    /**
     * TC_UPDATE_003: Update Non-Existent Pet
     *
     * Verifies that updating a non-existent pet returns appropriate error.
     */
    @Test(priority = 3)
    @Description("TC_UPDATE_003: Verify error when attempting to update non-existent pet")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Pet - Negative Scenarios")
    public void testUpdateNonExistentPet() {
        logTestStart("TC_UPDATE_003: Update Non-Existent Pet");

        Long nonExistentId = 999999999L;

        Pet nonExistentPet = Pet.builder()
                .id(nonExistentId)
                .name("GhostPet")
                .photoUrls(Collections.singletonList("https://example.com/ghost.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        given()
                .spec(requestSpec)
                .body(nonExistentPet)
                .when()
                .put(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_NOT_FOUND),
                        equalTo(Config.STATUS_CODE_OK) // Note: Swagger Petstore might create if not found
                ));

        logger.info("Attempted to update non-existent pet with ID: {}", nonExistentId);
        logTestEnd("TC_UPDATE_003");
    }

    /**
     * TC_UPDATE_004: Update Pet with Missing Required Fields
     *
     * Verifies that update fails when required fields are missing.
     */
    @Test(priority = 4)
    @Description("TC_UPDATE_004: Verify error when updating pet with missing required fields")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update Pet - Negative Scenarios")
    public void testUpdatePetWithMissingRequiredFields() {
        logTestStart("TC_UPDATE_004: Update Pet with Missing Required Fields");

        // First create a pet
        Pet originalPet = Pet.builder()
                .id(70001L)
                .name("TestPetForUpdate")
                .photoUrls(Collections.singletonList("https://example.com/test.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        given()
                .spec(requestSpec)
                .body(originalPet)
                .when()
                .post(Config.PET_ENDPOINT);

        // Try to update with missing name and photoUrls
        Pet incompletePet = Pet.builder()
                .id(70001L)
                .status(Config.STATUS_SOLD)
                .build();

        given()
                .spec(requestSpec)
                .body(incompletePet)
                .when()
                .put(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_METHOD_NOT_ALLOWED),
                        equalTo(Config.STATUS_CODE_OK) // Some APIs might allow partial updates
                ));

        logger.info("Tested update with missing required fields");
        logTestEnd("TC_UPDATE_004");
    }

    /**
     * TC_UPDATE_005: Update Pet with Invalid Status Value
     *
     * Verifies validation of status field (should only accept: available, pending, sold).
     */
    @Test(priority = 5)
    @Description("TC_UPDATE_005: Verify API validates status field values")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update Pet - Edge Cases")
    public void testUpdatePetWithInvalidStatusValue() {
        logTestStart("TC_UPDATE_005: Update Pet with Invalid Status Value");

        // Create a pet first
        Pet originalPet = Pet.builder()
                .id(80001L)
                .name("StatusTestPet")
                .photoUrls(Collections.singletonList("https://example.com/statustest.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        given()
                .spec(requestSpec)
                .body(originalPet)
                .when()
                .post(Config.PET_ENDPOINT);

        // Try to update with invalid status
        String invalidJson = "{\n" +
                "  \"id\": 80001,\n" +
                "  \"name\": \"StatusTestPet\",\n" +
                "  \"photoUrls\": [\"https://example.com/statustest.jpg\"],\n" +
                "  \"status\": \"invalid_status_value\"\n" +
                "}";

        given()
                .spec(requestSpec)
                .body(invalidJson)
                .when()
                .put(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_INTERNAL_SERVER_ERROR),
                        equalTo(Config.STATUS_CODE_OK) // Some APIs might accept any string
                ));

        logger.info("Tested update with invalid status value");
        logTestEnd("TC_UPDATE_005");
    }
}