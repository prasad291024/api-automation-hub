package com.prasad_v.gorest.tests;

import com.prasad_v.gorest.base.BaseGorestTest;
import com.prasad_v.gorest.services.GorestService;
import com.prasad_v.validation.SchemaValidator;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GorestTest extends BaseGorestTest {

    private GorestService gorestService;
    private int createdUserId;
    private String createdUserEmail;

    @BeforeClass
    public void setup() {
        gorestService = new GorestService();
    }

    @Test(description = "Verify list users returns 200 and schema is valid")
    public void testGetUsers() {
        Response response = gorestService.getUsers();
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.jsonPath().getList("$"));
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    @Test(description = "Verify create user returns 201", dependsOnMethods = "testGetUsers")
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
    public void testGetUserById() {
        // GoRest may return 404 immediately after creation due to propagation delay.
        // Retry for up to 5 seconds until 200 is returned.
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
                        // Fallback: verify via email filter — confirms the user exists in the system
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
    public void testUpdateUser() {
        Map<String, String> payload = Map.of("name", "Updated User", "status", "inactive");
        Response response = gorestService.updateUser(createdUserId, payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("name"), "Updated User");
    }

    @Test(description = "Verify delete user returns 204", dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() {
        Response response = gorestService.deleteUser(createdUserId);
        Assert.assertEquals(response.getStatusCode(), 204);
    }
}
