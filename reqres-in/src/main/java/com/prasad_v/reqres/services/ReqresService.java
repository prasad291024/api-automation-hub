package com.prasad_v.reqres.services;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.enums.RequestType;
import com.prasad_v.services.BaseApiService;
import io.restassured.response.Response;

import java.util.Map;

public class ReqresService extends BaseApiService {

    private final String baseUrl;
    private final String usersEndpoint;
    private final Map<String, String> apiKeyHeader;

    public ReqresService() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        this.baseUrl = config.getProperty("reqres.base.url", "https://reqres.in/api");
        this.usersEndpoint = config.getProperty("reqres.users.endpoint", "/users");
        String apiKey = config.getProperty("reqres.api.key",
                "pub_3006d5b8b1719b6d8e75f2404f3366fb7f1893f9b7a3804f4f1064b5edcea630");
        this.apiKeyHeader = Map.of("x-api-key", apiKey);
    }

    public Response getUsers(int page) {
        return execute(RequestType.GET, baseUrl + usersEndpoint + "?page=" + page, apiKeyHeader, null);
    }

    public Response getUserById(int id) {
        return execute(RequestType.GET, baseUrl + usersEndpoint + "/" + id, apiKeyHeader, null);
    }

    public Response createUser(Object payload) {
        return execute(RequestType.POST, baseUrl + usersEndpoint, apiKeyHeader, payload);
    }

    public Response updateUser(int id, Object payload) {
        return execute(RequestType.PUT, baseUrl + usersEndpoint + "/" + id, apiKeyHeader, payload);
    }

    public Response deleteUser(int id) {
        return execute(RequestType.DELETE, baseUrl + usersEndpoint + "/" + id, apiKeyHeader, null);
    }
}
