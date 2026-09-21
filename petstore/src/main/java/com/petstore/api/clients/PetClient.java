package com.petstore.api.clients;

import com.petstore.api.models.Pet;
import com.petstore.api.services.PetService;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

/**
 * Domain-specific client abstraction for Swagger Petstore operations.
 * Provides strongly-typed CRUD methods returning Pet entities.
 */
public class PetClient {

    private final PetService petService;

    public PetClient() {
        this.petService = new PetService();
    }

    public PetClient(PetService petService) {
        this.petService = petService;
    }

    /**
     * Creates a new pet and returns the typed Pet response.
     *
     * @param pet Pet data to create
     * @return Created Pet object
     */
    public Pet createPet(Pet pet) {
        Response response = petService.createPet(pet);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to create pet. Status: " + response.getStatusCode()
                    + ", Body: " + response.asString());
        }
        return response.as(Pet.class);
    }

    /**
     * Retrieves a pet by ID.
     *
     * @param petId ID of the pet
     * @return Retrieved Pet object
     */
    public Pet getPet(long petId) {
        Response response = petService.getPetById(petId);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to get pet " + petId + ". Status: " + response.getStatusCode());
        }
        return response.as(Pet.class);
    }

    /**
     * Updates an existing pet.
     *
     * @param pet Pet object with updated values
     * @return Updated Pet object
     */
    public Pet updatePet(Pet pet) {
        Response response = petService.updatePet(pet);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to update pet. Status: " + response.getStatusCode());
        }
        return response.as(Pet.class);
    }

    /**
     * Deletes a pet by ID.
     *
     * @param petId ID of the pet to delete
     * @return true if deletion succeeded (HTTP 200)
     */
    public boolean deletePet(long petId) {
        Response response = petService.deletePet(petId);
        return response.getStatusCode() == 200;
    }

    /**
     * Finds pets by status.
     *
     * @param status status filter (available, pending, sold)
     * @return List of matching pets
     */
    public List<Pet> findByStatus(String status) {
        Response response = petService.findPetsByStatus(status);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to find pets by status. Status: " + response.getStatusCode());
        }
        Pet[] pets = response.as(Pet[].class);
        return Arrays.asList(pets);
    }

    public PetService getService() {
        return petService;
    }
}
