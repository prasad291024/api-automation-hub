package com.prasad_v.reqres.asserts;

import com.prasad_v.reqres.models.ReqresUser;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

public class ReqresAssertions {

    @Step("Verify status code is {expectedStatusCode}")
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());
    }

    @Step("Verify user created: name={expectedName}, job={expectedJob}")
    public static void assertUserCreated(ReqresUser user, String expectedName, String expectedJob) {
        Assert.assertNotNull(user, "User should not be null");
        Assert.assertNotNull(user.getId(), "User ID should be generated");
        Assert.assertEquals(user.getName(), expectedName, "User name should match");
        Assert.assertEquals(user.getJob(), expectedJob, "User job should match");
    }

    @Step("Verify user updated: job={expectedJob}")
    public static void assertUserUpdated(ReqresUser user, String expectedJob) {
        Assert.assertNotNull(user, "User should not be null");
        Assert.assertEquals(user.getJob(), expectedJob, "User job should match updated value");
        Assert.assertNotNull(user.getUpdatedAt(), "UpdatedAt timestamp should be present");
    }
}
