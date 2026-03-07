package com.petstore.api.tests;

import com.petstore.api.base.BaseTest;
import com.petstore.api.config.Config;
import com.petstore.api.models.Pet;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * End-to-End Flow Test
 *
 * This class contains the complete pet lifecycle test that covers:
 * Create → Retrieve → Update → Delete → Verify Deletion
 *
 * This is a critical integration test that validates the entire API workflow.
 *
 * @author Prasad_V
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - End-to-End Flow")
public class EndToEndFlowTest extends BaseTest {

    /**
     * TC_E2E_001: Complete Pet Lifecycle (Create → Get → Update → Delete)
     *
     * This test validates the complete lifecycle of a pet:
     * 1. Create a new pet and verify creation
     * 2. Retrieve the pet by ID and verify details
     * 3. Update the pet's information and verify the update
     * 4. Delete the pet and verify deletion
     * 5. Attempt to retrieve the deleted pet and verify 404 response
     *
     * This is the most comprehensive test in the suite.
     */
    @Test(priority = 1)
    @Description("TC_E2E_001: Complete Pet Lifecycle - Create, Retrieve, Update, Delete, and Verify")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Complete Pet Lifecycle")
    public void testCompletePetLifecycle() {
        logTestStart("TC_E2E_001: Complete Pet Lifecycle Test");

        Long petId;

        // ========== STEP 1: CREATE PET ==========
        logger.info("STEP 1: Creating a new pet");

        Pet newPet = Pet.builder()
                .id(99999L)
                .name("E2E Test Pet")
                .photoUrls(Collections.singletonList("https://example.com/e2e.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(newPet)
                .when()
                .post(Config.PET_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", notNullValue())
                .body("name", equalTo("E2E Test Pet"))
                .body("status", equalTo(Config.STATUS_AVAILABLE))
                .extract()
                .response();

        Pet createdPet = createResponse.as(Pet.class);
        petId = createdPet.getId();

        assertNotNull(petId, "Pet ID should not be null after creation");
        assertEquals(createdPet.getName(), "E2E Test Pet", "Pet name should match");
        assertEquals(createdPet.getStatus(), Config.STATUS_AVAILABLE, "Pet status should be 'available'");

        logger.info("✓ STEP 1 PASSED: Pet created successfully with ID: {}", petId);

        // ========== STEP 2: RETRIEVE PET ==========
        logger.info("STEP 2: Retrieving the created pet by ID");

        Response getResponse = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("id", equalTo(petId.intValue()))
                .body("name", equalTo("E2E Test Pet"))
                .body("status", equalTo(Config.STATUS_AVAILABLE))
                .extract()
                .response();

        Pet retrievedPet = getResponse.as(Pet.class);

        assertEquals(retrievedPet.getId(), petId, "Retrieved pet ID should match");
        assertEquals(retrievedPet.getName(), "E2E Test Pet", "Retrieved pet name should match");
        assertEquals(retrievedPet.getStatus(), Config.STATUS_AVAILABLE,
                "Retrieved pet status should be 'available'");

        logger.info("✓ STEP 2 PASSED: Pet retrieved successfully. Name: {}, Status: {}",
                retrievedPet.getName(), retrievedPet.getStatus());

        // ========== STEP 3: UPDATE PET ==========
        logger.info("STEP 3: Updating the pet's information");

        Pet updatedPet = Pet.builder()
                .id(petId)
                .name("E2E Updated Pet")
                .photoUrls(Collections.singletonList("https://example.com/e2e_updated.jpg"))
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
                .body("name", equalTo("E2E Updated Pet"))
                .body("status", equalTo(Config.STATUS_SOLD))
                .extract()
                .response();

        Pet resultPet = updateResponse.as(Pet.class);

        assertEquals(resultPet.getId(), petId, "Updated pet ID should remain the same");
        assertEquals(resultPet.getName(), "E2E Updated Pet", "Pet name should be updated");
        assertEquals(resultPet.getStatus(), Config.STATUS_SOLD, "Pet status should be updated to 'sold'");

        logger.info("✓ STEP 3 PASSED: Pet updated successfully. New Name: {}, New Status: {}",
                resultPet.getName(), resultPet.getStatus());

        // Verify update by retrieving again
        Response getAfterUpdateResponse = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .statusCode(Config.STATUS_CODE_OK)
                .extract()
                .response();

        Pet verifiedUpdatedPet = getAfterUpdateResponse.as(Pet.class);

        assertEquals(verifiedUpdatedPet.getName(), "E2E Updated Pet",
                "Updated name should persist after retrieval");
        assertEquals(verifiedUpdatedPet.getStatus(), Config.STATUS_SOLD,
                "Updated status should persist after retrieval");

        logger.info("✓ STEP 3 VERIFIED: Updates persisted successfully");

        // ========== STEP 4: DELETE PET ==========
        logger.info("STEP 4: Deleting the pet");

        Response deleteResponse = given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .delete(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .extract()
                .response();

        logger.info("✓ STEP 4 PASSED: Pet deleted successfully. Response: {}",
                deleteResponse.jsonPath().getString("message"));

        // ========== STEP 5: VERIFY DELETION ==========
        logger.info("STEP 5: Verifying pet deletion by attempting to retrieve");

        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .when()
                .get(Config.PET_BY_ID_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_NOT_FOUND)
                .body("message", containsStringIgnoringCase("not found"));

        logger.info("✓ STEP 5 PASSED: Pet with ID {} cannot be retrieved (404 - Not Found)", petId);

        // ========== TEST COMPLETION ==========
        logger.info("========================================");
        logger.info("✓✓✓ END-TO-END FLOW TEST COMPLETED SUCCESSFULLY ✓✓✓");
        logger.info("All steps passed: Create → Retrieve → Update → Delete → Verify Deletion");
        logger.info("========================================");

        logTestEnd("TC_E2E_001: Complete Pet Lifecycle");
    }

    /**
     * Additional E2E Test: Multiple Status Transitions
     *
     * Tests a pet going through multiple status transitions in sequence.
     */
    @Test(priority = 2)
    @Description("Additional E2E Test: Pet with multiple status transitions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Pet Status Transitions")
    public void testPetLifecycleWithMultipleStatusTransitions() {
        logTestStart("Additional E2E Test: Multiple Status Transitions");

        // Create pet with "available" status
        Pet pet = Pet.builder()
                .name("Status Transition Pet")
                .photoUrls(Collections.singletonList("https://example.com/transition.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Response createResponse = given()
                .spec(requestSpec)
                .body(pet)
                .post(Config.PET_ENDPOINT);

        Long petId = createResponse.jsonPath().getLong("id");
        logger.info("Created pet with ID: {} and status: available", petId);

        // Transition 1: available → pending
        Pet pendingPet = Pet.builder()
                .id(petId)
                .name("Status Transition Pet")
                .photoUrls(Collections.singletonList("https://example.com/transition.jpg"))
                .status(Config.STATUS_PENDING)
                .build();

        given()
                .spec(requestSpec)
                .body(pendingPet)
                .put(Config.PET_ENDPOINT)
                .then()
                .statusCode(Config.STATUS_CODE_OK)
                .body("status", equalTo(Config.STATUS_PENDING));

        logger.info("Transitioned pet status: available → pending");

        // Transition 2: pending → sold
        Pet soldPet = Pet.builder()
                .id(petId)
                .name("Status Transition Pet")
                .photoUrls(Collections.singletonList("https://example.com/transition.jpg"))
                .status(Config.STATUS_SOLD)
                .build();

        given()
                .spec(requestSpec)
                .body(soldPet)
                .put(Config.PET_ENDPOINT)
                .then()
                .statusCode(Config.STATUS_CODE_OK)
                .body("status", equalTo(Config.STATUS_SOLD));

        logger.info("Transitioned pet status: pending → sold");

        // Cleanup
        given()
                .spec(requestSpec)
                .pathParam("petId", petId)
                .delete(Config.PET_BY_ID_ENDPOINT);

        logger.info("✓ Multiple status transitions test completed successfully");
        logTestEnd("Additional E2E Test: Multiple Status Transitions");
    }
}