package com.prasad_v.tests.base;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.config.EnvironmentManager;
import com.prasad_v.asserts.AssertActions;
import com.prasad_v.interceptors.RequestResponseInterceptor;
import com.prasad_v.modules.PayloadManager;
import com.prasad_v.clients.AuthClient;
import com.prasad_v.clients.BookingClient;
import com.prasad_v.services.AuthService;
import com.prasad_v.services.BookingService;
import com.prasad_v.utils.DataGenerator;
import com.prasad_v.utils.RestUtils;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest class provides a foundation for all API tests.
 * Supports both modern service layer abstractions and backward-compatible
 * fields for legacy tests.
 */
public class BaseTest {
    protected ConfigurationManager config;
    public PayloadManager payloadManager;
    public AssertActions assertActions;
    public DataGenerator dataGenerator;

    // Service layer abstractions
    protected BookingService bookingService;
    protected AuthService authService;

    // Client layer abstractions
    protected BookingClient bookingClient;
    protected AuthClient authClient;

    // Legacy fields maintained for backward compatibility
    @Deprecated
    public RequestSpecification requestSpecification;
    @Deprecated
    public JsonPath jsonPath;
    @Deprecated
    public Response response;
    @Deprecated
    public ValidatableResponse validatableResponse;

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        config = ConfigurationManager.getInstance();
        payloadManager = new PayloadManager();
        assertActions = new AssertActions();
        dataGenerator = new DataGenerator();

        // Initialize service layer components
        bookingService = new BookingService();
        authService = new AuthService();

        // Initialize client layer components
        authClient = new AuthClient(authService);
        bookingClient = new BookingClient(bookingService, authClient);

        // Legacy RequestSpecification setup
        String baseUrl = config.getProperty("api.base.url", APIConstants.BASE_URL);
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .addFilter(new RequestResponseInterceptor())
                .build();
    }

    /**
     * Retrieve authentication token using AuthService or legacy fallback.
     *
     * @return Auth token string
     */
    public String getToken() {
        if (authService != null) {
            return authService.getAuthToken();
        }
        String baseUrl = config.getProperty("api.base.url", APIConstants.BASE_URL);
        requestSpecification.baseUri(baseUrl).basePath(APIConstants.AUTH_URL).contentType(ContentType.JSON);
        response = RestUtils.post(requestSpecification, payloadManager.setAuthPayload());
        return payloadManager.getTokenFromJSON(response.asString());
    }

    // Service getters for subclasses
    public BookingService getBookingService() {
        return bookingService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public DataGenerator getDataGenerator() {
        return dataGenerator;
    }

    public BookingClient getBookingClient() {
        return bookingClient;
    }

    public AuthClient getAuthClient() {
        return authClient;
    }
}