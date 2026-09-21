package com.prasad_v.dummy.asserts;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

public class DummyRestAssertions {

    @Step("Verify status code is {expectedStatusCode}")
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());
    }

    @Step("Verify response status key is {expectedStatus}")
    public static void assertResponseStatus(Response response, String expectedStatus) {
        Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus,
                "Expected response status: " + expectedStatus);
    }

    @Step("Verify employee created with name: {expectedName}")
    public static void assertEmployeeCreated(Response response, String expectedName) {
        assertStatusCode(response, 200);
        assertResponseStatus(response, "success");
        Assert.assertEquals(response.jsonPath().getString("data.name"), expectedName,
                "Employee name should match");
        Assert.assertNotNull(response.jsonPath().getString("data.id"),
                "Employee ID should not be null");
    }

    @Step("Verify employee updated with name: {expectedName}")
    public static void assertEmployeeUpdated(Response response, String expectedName) {
        assertStatusCode(response, 200);
        assertResponseStatus(response, "success");
        Assert.assertEquals(response.jsonPath().getString("data.name"), expectedName,
                "Updated employee name should match");
    }

    @Step("Verify employee deleted: id={deletedId}")
    public static void assertEmployeeDeleted(Response response) {
        assertStatusCode(response, 200);
        assertResponseStatus(response, "success");
    }
}
