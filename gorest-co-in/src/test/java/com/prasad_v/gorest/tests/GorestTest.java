package com.prasad_v.gorest.tests;

import com.prasad_v.gorest.base.BaseGorestTest;
import com.prasad_v.gorest.services.GorestService;
import com.prasad_v.validation.SchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Epic("GoRest API")
@Feature("User Management")
@Owner("Prasad")
public class GorestTest extends BaseGorestTest {

    private GorestService gorestService;
    private int createdUserId;
    private String createdUserEmail;

    @BeforeClass
    public void setup() {
        gorestService = new GorestService();
    }

    @Test(description = "Verify list users returns 200 and schema is valid")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch all users and validate response against JSON schema")
    public void testGetUsers() {
        Response response = gorestService.getUsers();
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.jsonPath().getList("$"));
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    @Test(description = "Verify create user returns 201", dependsOnMethods = "testGetUsers")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new user with Bearer token auth and verify 201 response with ID")
    public void testCreateUser() {
        createdUserEmail = "testuser_" + System.currentTimeMillis() + "@example.com";
        Map<String, String> payload = Map.of(
                "name", "Test User",
                "email", createdUserEmail,
                "gender", "male",
                "status", "active"
        );
        Response response = gorestService.createUser(payload);
        Assert.assertEquals(response.getStatusCode(), 201,
                "Create user failed — verify gorest.auth.token is valid in config/dev.properties");
        createdUserId = response.jsonPath().getInt("id");
        Assert.assertTrue(createdUserId > 0);
    }

    @Test(description = "Verify get user by ID returns 200 with retry for propagation delay",
            dependsOnMethods = "testCreateUser")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch created user by ID with Awaitility retry; falls back to email filter if 404")
    public void testGetUserById() {
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    Response response = gorestService.getUserById(createdUserId);
                    int status = response.getStatusCode();
                    Assert.assertTrue(status == 200 || status == 404,
                            "Unexpected status: " + status);

                    if (status == 404) {
                        Response listResponse = gorestService.getUsersByEmail(createdUserEmail);
                        Assert.assertEquals(listResponse.getStatusCode(), 200);
                        List<Integer> ids = listResponse.jsonPath().getList("id");
                        Assert.assertTrue(ids != null && ids.contains(createdUserId),
                                "User ID " + createdUserId + " not found via email filter either");
                    } else {
                        Assert.assertEquals(response.jsonPath().getInt("id"), createdUserId);
                    }
                });
    }

    @Test(description = "Verify update user returns 200", dependsOnMethods = "testGetUserById")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update name and status of created user and verify 200 response")
    public void testUpdateUser() {
        Map<String, String> payload = Map.of("name", "Updated User", "status", "inactive");
        Response response = gorestService.updateUser(createdUserId, payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("name"), "Updated User");
    }

    @Test(description = "Verify delete user returns 204", dependsOnMethods = "testUpdateUser")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created user and verify 204 No Content response")
    public void testDeleteUser() {
        Response response = gorestService.deleteUser(createdUserId);
        Assert.assertEquals(response.getStatusCode(), 204);
    }
}
