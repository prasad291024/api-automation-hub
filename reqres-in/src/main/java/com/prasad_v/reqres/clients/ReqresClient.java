package com.prasad_v.reqres.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasad_v.exceptions.APIException;
import com.prasad_v.reqres.models.ReqresUser;
import com.prasad_v.reqres.services.ReqresService;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Client layer abstraction for ReqRes user management API.
 * Provides strongly typed CRUD operations and handles serialization/deserialization.
 */
public class ReqresClient {

    private static final Logger logger = LogManager.getLogger(ReqresClient.class);
    private final ReqresService reqresService;
    private final ObjectMapper objectMapper;

    public ReqresClient(ReqresService reqresService) {
        this.reqresService = reqresService;
        this.objectMapper = new ObjectMapper();
    }

    public ReqresClient() {
        this(new ReqresService());
    }

    public Response getUsersResponse(int page) {
        return reqresService.getUsers(page);
    }

    public ReqresUser getUser(int id) {
        Response response = reqresService.getUserById(id);
        if (response.getStatusCode() != 200) {
            throw new APIException("Failed to get user with id " + id + ", status: " + response.getStatusCode());
        }
        try {
            return response.jsonPath().getObject("data", ReqresUser.class);
        } catch (Exception e) {
            logger.error("Failed to parse user response", e);
            throw new APIException("Failed to parse user response", e);
        }
    }

    public Response getUserResponse(int id) {
        return reqresService.getUserById(id);
    }

    public ReqresUser createUser(ReqresUser user) {
        Response response = createUserResponse(user);
        if (response.getStatusCode() != 201) {
            throw new APIException("Failed to create user, status: " + response.getStatusCode());
        }
        return response.as(ReqresUser.class);
    }

    public Response createUserResponse(ReqresUser user) {
        return reqresService.createUser(user);
    }

    public Response createUserResponse(Map<String, ?> payload) {
        return reqresService.createUser(payload);
    }

    public ReqresUser updateUser(int id, ReqresUser user) {
        Response response = updateUserResponse(id, user);
        if (response.getStatusCode() != 200) {
            throw new APIException("Failed to update user with id " + id + ", status: " + response.getStatusCode());
        }
        return response.as(ReqresUser.class);
    }

    public Response updateUserResponse(int id, ReqresUser user) {
        return reqresService.updateUser(id, user);
    }

    public Response deleteUserResponse(int id) {
        return reqresService.deleteUser(id);
    }

    public ReqresService getService() {
        return reqresService;
    }
}
