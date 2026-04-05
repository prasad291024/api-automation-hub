package com.prasad_v.gorest.services;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.enums.RequestType;
import com.prasad_v.services.BaseApiService;
import io.restassured.response.Response;

import java.util.Map;

public class GorestService extends BaseApiService {

    private final String baseUrl;
    private final String usersEndpoint;
    private final String authToken;

    public GorestService() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        this.baseUrl = config.getProperty("gorest.base.url", "https://gorest.co.in/public/v2");
        this.usersEndpoint = config.getProperty("gorest.users.endpoint", "/users");
        this.authToken = config.getProperty("gorest.auth.token", "Bearer your_actual_token_here");
    }

    private Map<String, String> getCommonHeaders() {
        return Map.of(
            "Authorization", authToken,
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Accept", "application/json"
        );
    }

    public Response getUsers() {
        String path = baseUrl + usersEndpoint;
        return execute(RequestType.GET, path, getCommonHeaders(), null);
    }

    public Response createUser(Object payload) {
        String path = baseUrl + usersEndpoint;
        return execute(RequestType.POST, path, getCommonHeaders(), payload);
    }

    public Response createUserWithoutAuth(Object payload) {
        String path = baseUrl + usersEndpoint;
        return execute(RequestType.POST, path, null, payload);
    }

    public Response getUserById(int id) {
        String path = baseUrl + usersEndpoint + "/" + id;
        return execute(RequestType.GET, path, getCommonHeaders(), null);
    }

    public Response getUsersByEmail(String email) {
        String path = baseUrl + usersEndpoint + "?email=" + email;
        return execute(RequestType.GET, path, getCommonHeaders(), null);
    }

    public Response updateUser(int id, Object payload) {
        String path = baseUrl + usersEndpoint + "/" + id;
        return execute(RequestType.PUT, path, getCommonHeaders(), payload);
    }

    public Response deleteUser(int id) {
        String path = baseUrl + usersEndpoint + "/" + id;
        return execute(RequestType.DELETE, path, getCommonHeaders(), null);
    }
}
