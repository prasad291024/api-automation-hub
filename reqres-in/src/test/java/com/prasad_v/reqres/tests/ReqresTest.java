package com.prasad_v.reqres.tests;

import com.prasad_v.reqres.asserts.ReqresAssertions;
import com.prasad_v.reqres.base.BaseReqresTest;
import com.prasad_v.reqres.clients.ReqresClient;
import com.prasad_v.reqres.models.ReqresUser;
import com.prasad_v.utils.DataGenerator;
import com.prasad_v.validation.SchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("ReqRes API")
@Feature("User Management")
@Owner("Prasad")
public class ReqresTest extends BaseReqresTest {

    private ReqresClient reqresClient;
    private String createdUserId;
    private String randomUserName;
    private String initialJob;
    private String updatedJob;

    @BeforeClass
    public void setup() {
        reqresClient = new ReqresClient();
        randomUserName = DataGenerator.generateRandomFirstName();
        initialJob = "Engineer_" + DataGenerator.generateAlphanumeric(4);
        updatedJob = "Lead_" + DataGenerator.generateAlphanumeric(4);
    }

    // ── GET ──────────────────────────────────────────────────────────────────

    @Test(description = "Verify list users on page 2 returns 200")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch paginated user list from page 2 and verify response structure")
    public void testGetUsers() {
        Response response = reqresClient.getUsersResponse(2);
        ReqresAssertions.assertStatusCode(response, 200);
        Assert.assertNotNull(response.jsonPath().get("data"));
        Assert.assertEquals(response.jsonPath().getInt("page"), 2);
    }

    @Test(description = "Verify single user retrieval and schema validation")
    @Story("Get Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Fetch user by ID 2 and validate response against JSON schema")
    public void testGetSingleUser() {
        Response response = reqresClient.getUserResponse(2);
        ReqresAssertions.assertStatusCode(response, 200);
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2);
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify user creation returns 201")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new user and verify 201 response with returned ID using typed client")
    public void testCreateUser() {
        ReqresUser requestUser = ReqresUser.builder()
                .name(randomUserName)
                .job(initialJob)
                .build();

        ReqresUser createdUser = reqresClient.createUser(requestUser);
        ReqresAssertions.assertUserCreated(createdUser, randomUserName, initialJob);
        createdUserId = createdUser.getId();
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify user update returns 200", dependsOnMethods = "testCreateUser")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update job title of created user and verify updatedAt timestamp is returned")
    public void testUpdateUser() {
        ReqresUser updateRequest = ReqresUser.builder()
                .name(randomUserName)
                .job(updatedJob)
                .build();

        ReqresUser updatedUser = reqresClient.updateUser(Integer.parseInt(createdUserId), updateRequest);
        ReqresAssertions.assertUserUpdated(updatedUser, updatedJob);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify user deletion returns 204", dependsOnMethods = "testUpdateUser")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created user and verify 204 No Content response")
    public void testDeleteUser() {
        Response response = reqresClient.deleteUserResponse(Integer.parseInt(createdUserId));
        ReqresAssertions.assertStatusCode(response, 204);
    }

    // ── NEGATIVE ─────────────────────────────────────────────────────────────

    @Test(description = "Verify get non-existent user returns 404")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch user with non-existent ID 9999 and verify 404 Not Found response")
    public void testGetNonExistentUser() {
        Response response = reqresClient.getUserResponse(9999);
        ReqresAssertions.assertStatusCode(response, 404);
    }

    @Test(description = "Verify register with empty payload returns non-5xx")
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempt to create user with empty payload and verify non-5xx response")
    public void testCreateUserWithEmptyPayload() {
        Response response = reqresClient.createUserResponse(Map.of());
        Assert.assertTrue(response.getStatusCode() < 500,
                "Expected non-5xx response for empty payload, got: " + response.getStatusCode());
    }
}
