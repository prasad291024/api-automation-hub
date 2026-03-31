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
        this.authToken = config.getProperty("gorest.auth.token", "Bearer 61f22ca58528fe711f6c8d3c06eaf47168eecebb8fa25ba050d3ae76480b2e35");
    }

    public Response getUsers() {
        String path = baseUrl + usersEndpoint;
        return execute(RequestType.GET, path, null, null);
    }

    public Response createUser(Object payload) {
        String path = baseUrl + usersEndpoint;
        Map<String, String> headers = Map.of("Authorization", authToken);
        return execute(RequestType.POST, path, headers, payload);
    }

    public Response getUserById(int id) {
        String path = baseUrl + usersEndpoint + "/" + id;
        Map<String, String> headers = Map.of("Authorization", authToken);
        return execute(RequestType.GET, path, headers, null);
    }

    public Response getUsersByEmail(String email) {
        String path = baseUrl + usersEndpoint + "?email=" + email;
        Map<String, String> headers = Map.of("Authorization", authToken);
        return execute(RequestType.GET, path, headers, null);
    }

    public Response updateUser(int id, Object payload) {
        String path = baseUrl + usersEndpoint + "/" + id;
        Map<String, String> headers = Map.of("Authorization", authToken);
        return execute(RequestType.PUT, path, headers, payload);
    }

    public Response deleteUser(int id) {
        String path = baseUrl + usersEndpoint + "/" + id;
        Map<String, String> headers = Map.of("Authorization", authToken);
        return execute(RequestType.DELETE, path, headers, null);
    }
}
