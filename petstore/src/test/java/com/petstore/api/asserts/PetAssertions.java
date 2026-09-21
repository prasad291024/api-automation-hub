package com.petstore.api.asserts;

import com.petstore.api.models.Pet;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Domain-specific assertion utility for Petstore API tests.
 * Provides clear assertions with Allure step visibility.
 */
public class PetAssertions {

    @Step("Verify API status code is {expectedStatusCode}")
    public static void verifyStatusCode(Response response, int expectedStatusCode) {
        assertNotNull(response, "Response should not be null");
        assertEquals(response.getStatusCode(), expectedStatusCode, "Status code does not match expected");
    }

    @Step("Verify created pet response details")
    public static void verifyPetCreated(Response response, Pet expectedPet) {
        verifyStatusCode(response, 200);
        Pet actualPet = response.as(Pet.class);
        verifyPetDetails(actualPet, expectedPet);
    }

    @Step("Verify pet details match expected data")
    public static void verifyPetDetails(Pet actualPet, Pet expectedPet) {
        assertNotNull(actualPet, "Actual pet cannot be null");
        assertNotNull(actualPet.getId(), "Pet ID must be generated");
        assertEquals(actualPet.getName(), expectedPet.getName(), "Pet name does not match");
        assertEquals(actualPet.getStatus(), expectedPet.getStatus(), "Pet status does not match");

        if (expectedPet.getCategory() != null && actualPet.getCategory() != null) {
            assertEquals(actualPet.getCategory().getName(), expectedPet.getCategory().getName(),
                    "Pet category does not match");
        }
    }

    @Step("Verify pet status is {expectedStatus}")
    public static void verifyPetStatus(Pet pet, String expectedStatus) {
        assertNotNull(pet, "Pet cannot be null");
        assertEquals(pet.getStatus(), expectedStatus, "Pet status does not match expected");
    }
}
