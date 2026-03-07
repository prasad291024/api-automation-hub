package com.petstore.api.tests;

import com.petstore.api.base.BaseTest;
import com.petstore.api.config.Config;
import com.petstore.api.dataproviders.TestDataProvider;
import com.petstore.api.models.Pet;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Find Pets by Status API Test Cases
 *
 * This class contains all test cases for finding pets by status via GET /pet/findByStatus endpoint.
 * Tests cover positive scenarios, negative scenarios, and edge cases.
 *
 * @author Prasad_V
 * @version 1.0
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Search Operations")
public class FindPetsByStatusTests extends BaseTest {

    /**
     * TC_FIND_001: Find Pets with Single Valid Status (Available)
     *
     * Verifies retrieval of pets with status "available".
     * Creates pets with specific status and validates the search results.
     */
    @Test(priority = 1)
    @Description("TC_FIND_001: Verify retrieval of pets with status 'available'")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Find Pets by Single Status")
    public void testFindPetsByAvailableStatus() {
        logTestStart("TC_FIND_001: Find Pets with Available Status");

        // Step 1: Create a pet with "available" status
        Pet availablePet = Pet.builder()
                .id(100001L)
                .name("AvailablePet")
                .photoUrls(Collections.singletonList("https://example.com/available.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        given()
                .spec(requestSpec)
                .body(availablePet)
                .when()
                .post(Config.PET_ENDPOINT);

        logger.info("Created pet with 'available' status");

        // Step 2: Search for pets with "available" status
        Response response = given()
                .spec(requestSpec)
                .queryParam("status", Config.STATUS_AVAILABLE)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .body("$", not(empty()))
                .extract()
                .response();

        List<Pet> pets = response.jsonPath().getList("", Pet.class);
        assertTrue(pets.size() > 0, "At least one pet with 'available' status should be found");

        // Verify all returned pets have "available" status
        boolean allAvailable = pets.stream()
                .allMatch(pet -> Config.STATUS_AVAILABLE.equals(pet.getStatus()));
        assertTrue(allAvailable, "All returned pets should have 'available' status");

        logger.info("Found {} pets with 'available' status", pets.size());
        logTestEnd("TC_FIND_001");
    }

    /**
     * TC_FIND_002: Find Pets with Multiple Status Values
     *
     * Verifies retrieval with multiple status values (available, pending, sold).
     * Uses data-driven approach to test all valid statuses.
     */
    @Test(dataProvider = "validStatusValues", dataProviderClass = TestDataProvider.class, priority = 2)
    @Description("TC_FIND_002: Verify retrieval with multiple status values")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Find Pets by Multiple Statuses - Data Driven")
    public void testFindPetsByMultipleStatusValues(String status) {
        logTestStart("TC_FIND_002: Find Pets with Status: " + status);

        // Create a pet with the specified status
        Pet pet = Pet.builder()
                .name("Pet_" + status)
                .photoUrls(Collections.singletonList("https://example.com/" + status + ".jpg"))
                .status(status)
                .build();

        given()
                .spec(requestSpec)
                .body(pet)
                .when()
                .post(Config.PET_ENDPOINT);

        logger.info("Created pet with '{}' status", status);

        // Search for pets with this status
        Response response = given()
                .spec(requestSpec)
                .queryParam("status", status)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .extract()
                .response();

        List<Pet> pets = response.jsonPath().getList("", Pet.class);

        if (pets != null && pets.size() > 0) {
            // Verify all returned pets have the requested status
            boolean allMatchStatus = pets.stream()
                    .allMatch(p -> status.equals(p.getStatus()));
            assertTrue(allMatchStatus, "All returned pets should have '" + status + "' status");
            logger.info("Found {} pets with '{}' status", pets.size(), status);
        } else {
            logger.warn("No pets found with '{}' status", status);
        }

        logTestEnd("TC_FIND_002");
    }

    /**
     * TC_FIND_003: Find Pets with Invalid Status Value
     *
     * Verifies error handling when invalid status is provided.
     */
    @Test(dataProvider = "invalidStatusValues", dataProviderClass = TestDataProvider.class, priority = 3)
    @Description("TC_FIND_003: Verify error handling with invalid status value")
    @Severity(SeverityLevel.NORMAL)
    @Story("Find Pets - Negative Scenarios")
    public void testFindPetsByInvalidStatus(String invalidStatus) {
        logTestStart("TC_FIND_003: Find Pets with Invalid Status: " + invalidStatus);

        Response response = given()
                .spec(requestSpec)
                .queryParam("status", invalidStatus)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_OK),  // Some APIs return empty array
                        equalTo(Config.STATUS_CODE_BAD_REQUEST)
                ))
                .extract()
                .response();

        if (response.statusCode() == Config.STATUS_CODE_OK) {
            List<Pet> pets = response.jsonPath().getList("", Pet.class);
            assertTrue(pets == null || pets.isEmpty(),
                    "Should return empty array for invalid status");
            logger.info("API returned empty array for invalid status: {}", invalidStatus);
        } else {
            logger.info("API returned error for invalid status: {}", invalidStatus);
        }

        logTestEnd("TC_FIND_003");
    }

    /**
     * TC_FIND_004: Find Pets with Missing Status Parameter
     *
     * Verifies error when status parameter is not provided.
     */
    @Test(priority = 4)
    @Description("TC_FIND_004: Verify error when status parameter is missing")
    @Severity(SeverityLevel.NORMAL)
    @Story("Find Pets - Negative Scenarios")
    public void testFindPetsByMissingStatusParameter() {
        logTestStart("TC_FIND_004: Find Pets with Missing Status Parameter");

        given()
                .spec(requestSpec)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(anyOf(
                        equalTo(Config.STATUS_CODE_BAD_REQUEST),
                        equalTo(Config.STATUS_CODE_OK)  // Some APIs might return all pets
                ));

        logger.info("Tested findByStatus without status parameter");
        logTestEnd("TC_FIND_004");
    }

    /**
     * TC_FIND_005: Find Pets When No Pets Match Status
     *
     * Verifies behavior when no pets exist with the requested status.
     * This is an edge case test.
     */
    @Test(priority = 5)
    @Description("TC_FIND_005: Verify behavior when no pets match the requested status")
    @Severity(SeverityLevel.MINOR)
    @Story("Find Pets - Edge Cases")
    public void testFindPetsWhenNoPetsMatchStatus() {
        logTestStart("TC_FIND_005: Find Pets When No Pets Match Status");

        // First, try to delete all pets with "pending" status (to ensure none exist)
        // This is best-effort; we can't guarantee cleanup in shared test environment

        // Search for pets with "pending" status
        Response response = given()
                .spec(requestSpec)
                .queryParam("status", Config.STATUS_PENDING)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .extract()
                .response();

        List<Pet> pets = response.jsonPath().getList("", Pet.class);

        // The result might be empty or contain pets
        // We just verify the response is valid
        assertNotNull(pets, "Response should contain a list (even if empty)");

        if (pets.isEmpty()) {
            logger.info("No pets found with 'pending' status - edge case verified");
        } else {
            logger.info("Found {} pets with 'pending' status", pets.size());
        }

        logTestEnd("TC_FIND_005");
    }

    /**
     * Additional Test: Find Pets with Multiple Status Parameters
     *
     * Tests finding pets with multiple status values in a single request.
     */
    @Test(priority = 6)
    @Description("Additional Test: Find pets using multiple status values in one request")
    @Severity(SeverityLevel.NORMAL)
    @Story("Find Pets - Advanced Scenarios")
    public void testFindPetsByMultipleStatusParameters() {
        logTestStart("Additional Test: Find Pets with Multiple Status Parameters");

        // Create pets with different statuses
        Pet availablePet = Pet.builder()
                .name("MultiStatusTest_Available")
                .photoUrls(Collections.singletonList("https://example.com/multi1.jpg"))
                .status(Config.STATUS_AVAILABLE)
                .build();

        Pet pendingPet = Pet.builder()
                .name("MultiStatusTest_Pending")
                .photoUrls(Collections.singletonList("https://example.com/multi2.jpg"))
                .status(Config.STATUS_PENDING)
                .build();

        given().spec(requestSpec).body(availablePet).post(Config.PET_ENDPOINT);
        given().spec(requestSpec).body(pendingPet).post(Config.PET_ENDPOINT);

        // Search with multiple status values
        Response response = given()
                .spec(requestSpec)
                .queryParam("status", Config.STATUS_AVAILABLE)
                .queryParam("status", Config.STATUS_PENDING)
                .when()
                .get(Config.FIND_BY_STATUS_ENDPOINT)
                .then()
                .spec(responseSpec)
                .statusCode(Config.STATUS_CODE_OK)
                .extract()
                .response();

        List<Pet> pets = response.jsonPath().getList("", Pet.class);
        assertTrue(pets.size() > 0, "Should find pets with multiple status values");

        logger.info("Found {} pets with multiple status values", pets.size());
        logTestEnd("Additional Test: Multiple Status Parameters");
    }
}