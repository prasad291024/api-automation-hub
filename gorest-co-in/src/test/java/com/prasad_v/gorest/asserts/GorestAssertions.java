package com.prasad_v.gorest.asserts;

import com.prasad_v.gorest.models.GorestUser;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

public class GorestAssertions {

    @Step("Verify status code is {expectedStatusCode}")
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());
    }

    @Step("Verify status code is one of {expectedStatusCodes}")
    public static void assertStatusCodeIn(Response response, int... expectedStatusCodes) {
        int actual = response.getStatusCode();
        boolean match = false;
        for (int expected : expectedStatusCodes) {
            if (actual == expected) {
                match = true;
                break;
            }
        }
        Assert.assertTrue(match, "Expected status code to be one of " + java.util.Arrays.toString(expectedStatusCodes) + " but got: " + actual);
    }

    @Step("Verify user created: name={expectedName}, email={expectedEmail}")
    public static void assertUserCreated(GorestUser user, String expectedName, String expectedEmail) {
        Assert.assertNotNull(user, "User should not be null");
        Assert.assertNotNull(user.getId(), "User ID should be generated");
        Assert.assertTrue(user.getId() > 0, "User ID should be positive");
        Assert.assertEquals(user.getName(), expectedName, "User name should match");
        Assert.assertEquals(user.getEmail(), expectedEmail, "User email should match");
    }

    @Step("Verify user updated: name={expectedName}, status={expectedStatus}")
    public static void assertUserUpdated(GorestUser user, String expectedName, String expectedStatus) {
        Assert.assertNotNull(user, "User should not be null");
        Assert.assertEquals(user.getName(), expectedName, "User name should match updated name");
        Assert.assertEquals(user.getStatus(), expectedStatus, "User status should match updated status");
    }
}
