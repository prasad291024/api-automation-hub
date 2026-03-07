package com.petstore.api.config;

/**
 * Configuration class containing API base URL and endpoints
 */
public class Config {

    // Base URL for Swagger Petstore API
    public static final String BASE_URL = "https://petstore.swagger.io/v2";

    // API Endpoints as per Documentation
    public static final String PET_ENDPOINT = "/pet";
    public static final String PET_BY_ID_ENDPOINT = "/pet/{petId}";
    public static final String FIND_BY_STATUS_ENDPOINT = "/pet/findByStatus";

    // Common HTTP Responce Status Codes
    public static final int STATUS_CODE_OK = 200;
    public static final int STATUS_CODE_BAD_REQUEST = 400;
    public static final int STATUS_CODE_NOT_FOUND = 404;
    public static final int STATUS_CODE_METHOD_NOT_ALLOWED = 405;
    public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    // Valid Pet Status Values
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SOLD = "sold";

    // Default Timeout in Seconds
    public static final int DEFAULT_TIMEOUT = 30;
}

