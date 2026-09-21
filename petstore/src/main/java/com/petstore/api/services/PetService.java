package com.petstore.api.services;

import com.petstore.api.config.Config;
import com.petstore.api.models.Pet;
import com.prasad_v.enums.RequestType;
import com.prasad_v.services.BaseApiService;
import io.restassured.response.Response;

/**
 * Service class for handling Pet-related API operations in Swagger Petstore.
 * Extends BaseApiService to follow framework standard request execution abstraction.
 */
public class PetService extends BaseApiService {

    private final String baseUrl;

    public PetService() {
        this.baseUrl = Config.BASE_URL;
    }

    public PetService(String baseUrl) {
        this.baseUrl = (baseUrl != null && !baseUrl.isBlank()) ? baseUrl : Config.BASE_URL;
    }

    /**
     * Sends a POST request to create a new pet.
     *
     * @param pet Pet object to be created
     * @return Response from the API
     */
    public Response createPet(Pet pet) {
        return execute(RequestType.POST, baseUrl + Config.PET_ENDPOINT, null, pet);
    }

    /**
     * Sends a GET request to retrieve a pet by its ID.
     *
     * @param petId ID of the pet
     * @return Response from the API
     */
    public Response getPetById(long petId) {
        return execute(RequestType.GET, baseUrl + Config.PET_ENDPOINT + "/" + petId, null, null);
    }

    /**
     * Sends a PUT request to update an existing pet.
     *
     * @param pet Pet object with updated information
     * @return Response from the API
     */
    public Response updatePet(Pet pet) {
        return execute(RequestType.PUT, baseUrl + Config.PET_ENDPOINT, null, pet);
    }

    /**
     * Sends a DELETE request to remove a pet by its ID.
     *
     * @param petId ID of the pet to delete
     * @return Response from the API
     */
    public Response deletePet(long petId) {
        return execute(RequestType.DELETE, baseUrl + Config.PET_ENDPOINT + "/" + petId, null, null);
    }

    /**
     * Sends a GET request to find pets by status.
     *
     * @param status status (available, pending, sold)
     * @return Response from the API
     */
    public Response findPetsByStatus(String status) {
        return execute(RequestType.GET, baseUrl + Config.FIND_BY_STATUS_ENDPOINT + "?status=" + status, null, null);
    }
}