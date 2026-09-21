package com.prasad_v.gorest.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasad_v.exceptions.APIException;
import com.prasad_v.gorest.models.GorestUser;
import com.prasad_v.gorest.services.GorestService;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Client layer abstraction for GoRest API.
 * Provides strongly typed CRUD operations and token management encapsulation.
 */
public class GorestClient {

    private static final Logger logger = LogManager.getLogger(GorestClient.class);
    private final GorestService gorestService;
    private final ObjectMapper objectMapper;

    public GorestClient(GorestService gorestService) {
        this.gorestService = gorestService;
        this.objectMapper = new ObjectMapper();
    }

    public GorestClient() {
        this(new GorestService());
    }

    public Response getUsersResponse() {
        return gorestService.getUsers();
    }

    public List<GorestUser> getUsers() {
        Response response = getUsersResponse();
        if (response.getStatusCode() != 200) {
            throw new APIException("Failed to get users, status: " + response.getStatusCode());
        }
        try {
            return objectMapper.readValue(response.asString(), new TypeReference<List<GorestUser>>() {});
        } catch (Exception e) {
            throw new APIException("Failed to deserialize users list", e);
        }
    }

    public GorestUser createUser(GorestUser user) {
        Response response = createUserResponse(user);
        if (response.getStatusCode() != 201) {
            throw new APIException("Failed to create user, status: " + response.getStatusCode() + ", body: " + response.asString());
        }
        return response.as(GorestUser.class);
    }

    public Response createUserResponse(GorestUser user) {
        return gorestService.createUser(user);
    }

    public Response createUserResponse(Map<String, ?> payload) {
        return gorestService.createUser(payload);
    }

    public Response createUserWithoutAuthResponse(Object payload) {
        return gorestService.createUserWithoutAuth(payload);
    }

    public GorestUser getUser(int id) {
        Response response = getUserResponse(id);
        if (response.getStatusCode() != 200) {
            throw new APIException("Failed to get user " + id + ", status: " + response.getStatusCode());
        }
        return response.as(GorestUser.class);
    }

    public Response getUserResponse(int id) {
        return gorestService.getUserById(id);
    }

    public Response getUsersByEmailResponse(String email) {
        return gorestService.getUsersByEmail(email);
    }

    public GorestUser updateUser(int id, GorestUser user) {
        Response response = updateUserResponse(id, user);
        if (response.getStatusCode() != 200) {
            throw new APIException("Failed to update user " + id + ", status: " + response.getStatusCode());
        }
        return response.as(GorestUser.class);
    }

    public Response updateUserResponse(int id, Object payload) {
        return gorestService.updateUser(id, payload);
    }

    public Response deleteUserResponse(int id) {
        return gorestService.deleteUser(id);
    }

    public GorestService getService() {
        return gorestService;
    }
}
