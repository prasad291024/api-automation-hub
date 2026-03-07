package com.petstore.api.services;

import com.petstore.api.models.Pet;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static com.petstore.api.config.Config.*;

/**
 * Service class for handling Pet-related API operations.
 *
 * This class uses RestAssured to send HTTP requests to the Swagger Petstore API.
 * Each method corresponds to a specific endpoint and encapsulates request logic.
 */
public class PetService {

    /**
     * Sends a POST request to create a new pet.
     *
     * @param pet Pet object to be created
     * @return Response from the API
     */
    public Response createPet(Pet pet) {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(pet)
                .post(PET_ENDPOINT);
    }

    // You can add more methods here later, like:
    // - getPetById(long petId)
    // - updatePet(Pet pet)
    // - deletePet(long petId)
    // - findPetsByStatus(String status)
}