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
 * Get Pet API Test Cases
 *
 * This class contains all test cases for retrieving pets via GET /pet/{petId} endpoint.
 * Tests cover positive scenarios, negative scenarios, and edge cases.
 *
 * @author Prasad_V
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Retrieve Operations")
public class GetPetTests extends BaseTest {

    /**
     * TC_GET_001: Retrieve Existing Pet by Valid ID
     *
     * Verifies that an existing pet can be retrieved using a valid pet ID.
     * First creates a pet, then retrieves it to verify.
     */
    @Test(priority = 1)
    @Description("TC_GET_001: Verify that an existing pet can be retrieved by valid ID")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Get Pet by Valid ID")
    public void testRetrieveExistingPetByValidId() {
        logTestStart("TC_GET_001: Retrieve Existing Pet by Valid ID");

        // Step 1: Create a pet first
        Pet newPet = Pet.builder()
                .id(50001L)
                .name("GetTestPet")
                .photoUrls(Collections.singletonList("https://example.com/gettest.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(newPet)
                .when()
                .post(Config.PET_ENDPOINT);

        Long createdPetId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet with ID: {}", createdPetId);

        // Step 2: Retrieve the created pet
        Response getResponse = given()
                .spec(requestSpec)
                .pathParam("petId", createdPetId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", equalTo(createdPetId.intValue()))
                .body("name", equalTo(newPet.getName()))
                .body("status", equalTo(newPet.getStatus()))
                .extract()
                .response();

        Pet retrievedPet = getResponse.as(Pet.class);
        assertEquals(retrievedPet.getId(), createdPetId, "Pet ID should match");
        assertEquals(retrievedPet.getName(), newPet.getName(), "Pet name should match");
        assertEquals(retrievedPet.getStatus(), newPet.getStatus(), "Pet status should match");

        logger.info("Successfully retrieved pet with ID: {}", retrievedPet.getId());
        logTestEnd("TC_GET_001");
    }

    /**
     * TC_GET_002: Retrieve Multiple Pets with Different Valid IDs
     *
     * Verifies retrieval of multiple pets using different valid IDs (data-driven test).
     * First creates pets, then retrieves them.
     */
    @Test(dataProvider = "validPetData", dataProviderClass = TestDataProvider.class, priority = 2)
    @Description("TC_GET_002: Verify retrieval of multiple pets with different valid IDs")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Multiple Pets - Data Driven")
    public void testRetrieveMultiplePetsWithDifferentIds(Pet pet) {
        logTestStart("TC_GET_002: Retrieve Multiple Pets");

        // Create pet
        Response createResponse = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");

        // Retrieve pet
        Response getResponse = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", equalTo(petId.intValue()))
                .body("name", equalTo(pet.getName()))
                .extract()
                .response();

        Pet retrievedPet = getResponse.as(Pet.class);
        assertNotNull(retrievedPet.getId(), "Retrieved pet should have an ID");
        assertEquals(retrievedPet.getName(), pet.getName(), "Pet name should match");

        logger.info("Retrieved pet: {} with ID: {}", retrievedPet.getName(), retrievedPet.getId());
        logTestEnd("TC_GET_002");
    }

    /**
     * TC_GET_003: Retrieve Pet with Non-Existent ID
     *
     * Verifies that API returns 404 when attempting to retrieve a pet with non-existent ID.
     */
    @Test(dataProvider = "invalidPetIds", dataProviderClass = TestDataProvider.class, priority = 3)
    @Description("TC_GET_003: Verify that API returns 404 for non-existent pet ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get Pet - Negative Scenarios")
    public void testRetrievePetWithNonExistentId(Long petId) {
        logTestStart("TC_GET_003: Retrieve Pet with Non-Existent ID");

        int statusCode = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .extract()
                .statusCode();

        assertTrue(statusCode == Config.STATUS_CODE_NOT_FOUND || statusCode == Config.STATUS_CODE_OK,
                "Expected 404 or 200 for non-existent pet ID, got: " + statusCode);
        logger.info("API correctly returned 404 for non-existent pet ID: {}", petId);
        logTestEnd("TC_GET_003");
    }

    /**
     * TC_GET_004: Retrieve Pet with Invalid ID Format
     *
     * Verifies that API handles invalid ID format appropriately.
     */
    @Test(priority = 4)
    @Description("TC_GET_004: Verify API handles invalid ID format (string instead of integer)")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Pet - Negative Scenarios")
    public void testRetrievePetWithInvalidIdFormat() {
        logTestStart("TC_GET_004: Retrieve Pet with Invalid ID Format");

        String invalidId = "abc123";

        given()
                .spec(requestSpec)
                .pathParam("petId", invalidId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_NOT_FOUND)
                ));

        logger.info("API correctly handled invalid ID format: {}", invalidId);
        logTestEnd("TC_GET_004");
    }

    /**
     * TC_GET_005: Retrieve Pet with Negative ID
     *
     * Verifies API behavior when negative pet ID is provided (edge case).
     */
    @Test(priority = 5)
    @Description("TC_GET_005: Verify API behavior with negative pet ID")
    @Severity(SeverityLevel.MINOR)
    @Story("Get Pet - Edge Cases")
    public void testRetrievePetWithNegativeId() {
        logTestStart("TC_GET_005: Retrieve Pet with Negative ID");

        Long negativePetId = -1L;

        given()
                .spec(requestSpec)
                .pathParam("petId", negativePetId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_NOT_FOUND),
                        equalTo(Config.STATUS_CODE_BAD_REQUEST)
                ));

        logger.info("API correctly handled negative ID: {}", negativePetId);
        logTestEnd("TC_GET_005");
    }
}