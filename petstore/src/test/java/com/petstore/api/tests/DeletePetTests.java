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
 * Delete Pet API Test Cases
 *
 * This class contains all test cases for deleting pets via DELETE /pet/{petId} endpoint.
 * Tests cover positive scenarios, negative scenarios, and edge cases.
 *
 * @author Prasad_V
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Delete Operations")
public class DeletePetTests extends BaseTest {

    /**
     * TC_DELETE_001: Delete Existing Pet by Valid ID
     *
     * Verifies that an existing pet can be deleted successfully.
     * Creates a pet, deletes it, then verifies deletion by attempting to retrieve it.
     */
    @Test(priority = 1)
    @Description("TC_DELETE_001: Verify that an existing pet can be deleted successfully")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Delete Pet with Valid ID")
    public void testDeleteExistingPetByValidId() {
        logTestStart("TC_DELETE_001: Delete Existing Pet by Valid ID");

        // Step 1: Create a pet to delete
        Pet petToDelete = Pet.builder()
                .id(90001L)
                .name("PetToDelete")
                .photoUrls(Collections.singletonList("https://example.com/delete.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(petToDelete)
                .when()
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet with ID: {} for deletion", petId);

        // Step 2: Delete the pet
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK);

        logger.info("Pet with ID: {} deleted successfully", petId);

        // Step 3: Verify deletion by attempting to retrieve the pet
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .statusCode(Config.STATUS_CODE_NOT_FOUND);

        logger.info("Verified that pet with ID: {} cannot be retrieved (404)", petId);
        logTestEnd("TC_DELETE_001");
    }

    /**
     * TC_DELETE_002: Delete Multiple Pets Sequentially
     *
     * Verifies deletion of multiple pets using different IDs (data-driven test).
     * Creates multiple pets, deletes them, and confirms they cannot be retrieved.
     */
    @Test(dataProvider = "validPetData", dataProviderClass = TestDataProvider.class, priority = 2)
    @Description("TC_DELETE_002: Verify deletion of multiple pets with different IDs")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Multiple Pets - Data Driven")
    public void testDeleteMultiplePetsSequentially(Pet pet) {
        logTestStart("TC_DELETE_002: Delete Multiple Pets");

        // Create pet
        Response createResponse = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet: {} with ID: {}", pet.getName(), petId);

        // Delete pet
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK);

        logger.info("Deleted pet with ID: {}", petId);

        // Verify deletion
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .statusCode(Config.STATUS_CODE_NOT_FOUND);

        logger.info("Verified pet with ID: {} is not retrievable after deletion", petId);
        logTestEnd("TC_DELETE_002");
    }

    /**
     * TC_DELETE_003: Delete Non-Existent Pet
     *
     * Verifies appropriate error when attempting to delete a non-existent pet.
     */
    @Test(dataProvider = "invalidPetIds", dataProviderClass = TestDataProvider.class, priority = 3)
    @Description("TC_DELETE_003: Verify error when attempting to delete non-existent pet")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Pet - Negative Scenarios")
    public void testDeleteNonExistentPet(Long nonExistentId) {
        logTestStart("TC_DELETE_003: Delete Non-Existent Pet");

        given()
                .spec(requestSpec)
                .pathParam("petId", nonExistentId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_NOT_FOUND);

        logger.info("API correctly returned 404 for non-existent pet ID: {}", nonExistentId);
        logTestEnd("TC_DELETE_003");
    }

    /**
     * TC_DELETE_004: Delete Pet with Invalid ID Format
     *
     * Verifies error handling when invalid ID format is provided for deletion.
     */
    @Test(priority = 4)
    @Description("TC_DELETE_004: Verify API handles invalid ID format during deletion")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Pet - Negative Scenarios")
    public void testDeletePetWithInvalidIdFormat() {
        logTestStart("TC_DELETE_004: Delete Pet with Invalid ID Format");

        String invalidId = "abc123";

        given()
                .spec(requestSpec)
                .pathParam("petId", invalidId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_NOT_FOUND)
                ));

        logger.info("API correctly handled invalid ID format: {}", invalidId);
        logTestEnd("TC_DELETE_004");
    }

    /**
     * TC_DELETE_005: Delete Already Deleted Pet
     *
     * Verifies behavior when attempting to delete a pet that was already deleted (edge case).
     */
    @Test(priority = 5)
    @Description("TC_DELETE_005: Verify behavior when deleting an already deleted pet")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Pet - Edge Cases")
    public void testDeleteAlreadyDeletedPet() {
        logTestStart("TC_DELETE_005: Delete Already Deleted Pet");

        // Step 1: Create a pet
        Pet pet = Pet.builder()
                .id(95001L)
                .name("DoubleDeleteTest")
                .photoUrls(Collections.singletonList("https://example.com/doubledelete.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet with ID: {}", petId);

        // Step 2: Delete the pet (first time)
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK);

        logger.info("First deletion successful for pet ID: {}", petId);

        // Step 3: Attempt to delete the same pet again
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_NOT_FOUND);

        logger.info("Second deletion attempt correctly returned 404 for pet ID: {}", petId);
        logTestEnd("TC_DELETE_005");
    }
}