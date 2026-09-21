package com.prasad_v.gorest.tests;

import com.prasad_v.gorest.asserts.GorestAssertions;
import com.prasad_v.gorest.base.BaseGorestTest;
import com.prasad_v.gorest.clients.GorestClient;
import com.prasad_v.gorest.models.GorestUser;
import com.prasad_v.utils.DataGenerator;
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

    private GorestClient gorestClient;
    private int createdUserId;
    private String createdUserEmail;
    private String initialUserName;
    private String updatedUserName;

    @BeforeClass
    public void setup() {
        gorestClient = new GorestClient();
        initialUserName = DataGenerator.generateRandomName();
        updatedUserName = "Updated " + DataGenerator.generateRandomFirstName();
        createdUserEmail = "testuser_" + System.currentTimeMillis() + "_" + DataGenerator.generateAlphanumeric(5) + "@example.com";
    }

    // ── GET ──────────────────────────────────────────────────────────────────

    @Test(description = "Verify list users returns 200 and schema is valid")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch all users and validate response against JSON schema")
    public void testGetUsers() {
        Response response = gorestClient.getUsersResponse();
        GorestAssertions.assertStatusCode(response, 200);
        Assert.assertNotNull(response.jsonPath().getList("$"));
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify create user returns 201", dependsOnMethods = "testGetUsers")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new user with Bearer token auth and verify 201 response with ID using typed client")
    public void testCreateUser() {
        GorestUser requestUser = GorestUser.builder()
                .name(initialUserName)
                .email(createdUserEmail)
                .gender("male")
                .status("active")
                .build();

        GorestUser createdUser = gorestClient.createUser(requestUser);
        GorestAssertions.assertUserCreated(createdUser, initialUserName, createdUserEmail);
        createdUserId = createdUser.getId();
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────

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
                    Response response = gorestClient.getUserResponse(createdUserId);
                    int status = response.getStatusCode();
                    Assert.assertTrue(status == 200 || status == 404,
                            "Unexpected status: " + status);

                    if (status == 404) {
                        Response listResponse = gorestClient.getUsersByEmailResponse(createdUserEmail);
                        Assert.assertEquals(listResponse.getStatusCode(), 200);
                        List<Integer> ids = listResponse.jsonPath().getList("id");
                        Assert.assertTrue(ids != null && ids.contains(createdUserId),
                                "User ID " + createdUserId + " not found via email filter either");
                    } else {
                        Assert.assertEquals(response.jsonPath().getInt("id"), createdUserId);
                    }
                });
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify update user returns 200", dependsOnMethods = "testGetUserById")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update name and status of created user and verify 200 response")
    public void testUpdateUser() {
        GorestUser updateRequest = GorestUser.builder()
                .name(updatedUserName)
                .status("inactive")
                .build();

        GorestUser updatedUser = gorestClient.updateUser(createdUserId, updateRequest);
        GorestAssertions.assertUserUpdated(updatedUser, updatedUserName, "inactive");
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify delete user returns 204", dependsOnMethods = "testUpdateUser")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created user and verify 204 No Content response")
    public void testDeleteUser() {
        Response response = gorestClient.deleteUserResponse(createdUserId);
        GorestAssertions.assertStatusCode(response, 204);
    }

    // ── NEGATIVE ─────────────────────────────────────────────────────────────

    @Test(description = "Verify create user without token returns 401 or 403")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Attempt to create user without Authorization header and verify 401 or 403 response")
    public void testCreateUserWithoutToken() {
        Map<String, String> payload = Map.of(
                "name", "Unauthorized User",
                "email", "unauth_" + System.currentTimeMillis() + "@example.com",
                "gender", "male",
                "status", "active"
        );
        Response response = gorestClient.createUserWithoutAuthResponse(payload);
        GorestAssertions.assertStatusCodeIn(response, 401, 403);
    }

    @Test(description = "Verify get non-existent user returns 404")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch user with non-existent ID 999999999 and verify 404 Not Found response")
    public void testGetNonExistentUser() {
        Response response = gorestClient.getUserResponse(999999999);
        GorestAssertions.assertStatusCode(response, 404);
    }
}
