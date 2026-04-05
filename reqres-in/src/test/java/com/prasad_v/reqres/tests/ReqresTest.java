package com.prasad_v.reqres.tests;

import com.prasad_v.reqres.base.BaseReqresTest;
import com.prasad_v.reqres.services.ReqresService;
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

    private ReqresService reqresService;
    private String createdUserId;

    @BeforeClass
    public void setup() {
        reqresService = new ReqresService();
    }

    @Test(description = "Verify list users on page 2 returns 200")
    @Story("Get Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch paginated user list from page 2 and verify response structure")
    public void testGetUsers() {
        Response response = reqresService.getUsers(2);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.jsonPath().get("data"));
        Assert.assertEquals(response.jsonPath().getInt("page"), 2);
    }

    @Test(description = "Verify single user retrieval and schema validation")
    @Story("Get Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Fetch user by ID 2 and validate response against JSON schema")
    public void testGetSingleUser() {
        Response response = reqresService.getUserById(2);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2);
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    @Test(description = "Verify user creation returns 201")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new user and verify 201 response with returned ID")
    public void testCreateUser() {
        Map<String, String> payload = Map.of(
                "name", "morpheus",
                "job", "leader"
        );
        Response response = reqresService.createUser(payload);
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.jsonPath().getString("name"), "morpheus");
        createdUserId = response.jsonPath().getString("id");
        Assert.assertNotNull(createdUserId);
    }

    @Test(description = "Verify user update returns 200", dependsOnMethods = "testCreateUser")
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update job title of created user and verify updatedAt timestamp is returned")
    public void testUpdateUser() {
        Map<String, String> payload = Map.of(
                "name", "morpheus",
                "job", "zion resident"
        );
        Response response = reqresService.updateUser(Integer.parseInt(createdUserId), payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("job"), "zion resident");
        Assert.assertNotNull(response.jsonPath().getString("updatedAt"));
    }

    @Test(description = "Verify user deletion returns 204", dependsOnMethods = "testUpdateUser")
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created user and verify 204 No Content response")
    public void testDeleteUser() {
        Response response = reqresService.deleteUser(Integer.parseInt(createdUserId));
        Assert.assertEquals(response.getStatusCode(), 204);
    }
}
