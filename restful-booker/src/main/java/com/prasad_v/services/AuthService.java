package com.prasad_v.services;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;
import com.prasad_v.modules.PayloadManager;
import io.restassured.response.Response;

/**
 * Service class for handling Authentication API operations for Restful-Booker.
 * Extends BaseApiService to utilize standard request execution abstractions.
 */
public class AuthService extends BaseApiService {

    private final PayloadManager payloadManager = new PayloadManager();

    /**
     * Executes POST request to /auth with given payload.
     *
     * @param payload authentication payload (JSON String, Map, or Auth POJO)
     * @return API Response
     */
    public Response createTokenResponse(Object payload) {
        return execute(RequestType.POST, APIConstants.AUTH_URL, null, payload);
    }

    /**
     * Generates a new auth token using default or configured credentials.
     *
     * @return authentication token string
     */
    public String getAuthToken() {
        String authPayload = payloadManager.setAuthPayload();
        Response response = createTokenResponse(authPayload);
        return payloadManager.getTokenFromJSON(response.asString());
    }

    /**
     * Generates an auth token for custom credentials.
     *
     * @param username username
     * @param password password
     * @return authentication token string
     */
    public String getAuthToken(String username, String password) {
        String authPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        Response response = createTokenResponse(authPayload);
        return payloadManager.getTokenFromJSON(response.asString());
    }
}
