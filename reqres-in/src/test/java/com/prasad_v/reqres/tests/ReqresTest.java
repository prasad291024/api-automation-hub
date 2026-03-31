package com.prasad_v.reqres.tests;

import com.prasad_v.reqres.base.BaseReqresTest;
import com.prasad_v.reqres.services.ReqresService;
import com.prasad_v.validation.SchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class ReqresTest extends BaseReqresTest {

    private ReqresService reqresService;
    private String createdUserId;

    @BeforeClass
    public void setup() {
        reqresService = new ReqresService();
    }

    @Test(description = "Verify list users on page 2 returns 200")
    public void testGetUsers() {
        Response response = reqresService.getUsers(2);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.jsonPath().get("data"));
        Assert.assertEquals(response.jsonPath().getInt("page"), 2);
    }

    @Test(description = "Verify single user retrieval and schema validation")
    public void testGetSingleUser() {
        Response response = reqresService.getUserById(2);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2);
        SchemaValidator.assertSchema(response, "user-schema.json");
    }

    @Test(description = "Verify user creation returns 201")
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
    public void testDeleteUser() {
        Response response = reqresService.deleteUser(Integer.parseInt(createdUserId));
        Assert.assertEquals(response.getStatusCode(), 204);
    }
}
