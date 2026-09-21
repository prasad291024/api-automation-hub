package com.prasad_v.asserts;

// Import RestAssured's Response class to access HTTP response details
import io.restassured.response.Response;

// Import TestNG's assertEquals for standard assertions
import static org.testng.Assert.assertEquals;

// Import AssertJ for fluent and readable assertions
import static org.assertj.core.api.Assertions.*;

public class AssertActions {

    /**
     * Validates that two String values are equal.
     * Useful for comparing expected vs actual values from the response body.
     *
     * @param actual      The actual value from response
     * @param expected    The expected value to compare
     * @param description Custom message to display if assertion fails
     */
    public void verifyResponseBody(String actual, String expected, String description) {
        assertEquals(actual, expected, description);
    }

    /**
     * Validates that two int values are equal.
     * Often used for numerical comparisons (like ID, total price, status codes, etc.)
     *
     * @param actual      The actual int value
     * @param expected    The expected int value
     * @param description Custom message if assertion fails
     */
    public void verifyResponseBody(int actual, int expected, String description) {
        assertEquals(actual, expected, description);
    }

    /**
     * Validates that two generic Objects are equal.
     *
     * @param actual      The actual value
     * @param expected    The expected value
     * @param description Custom message if assertion fails
     */
    public void verifyResponseBody(Object actual, Object expected, String description) {
        assertEquals(actual, expected, description);
    }

    /**
     * Validates the status code of an API response.
     *
     * @param response The RestAssured response object
     * @param expected The expected status code (e.g., 200, 201, 404)
     */
    public void verifyStatusCode(Response response, Integer expected) {
        assertEquals(Integer.valueOf(response.getStatusCode()), expected);
    }

    /**
     * Validates that a string value:
     * - Is not null
     * - Is not blank
     * - Matches the expected value
     *
     * @param keyExpect The expected value
     * @param keyActual The actual value from response
     */
    public void verifyStringKey(String keyExpect, String keyActual) {
        assertThat(keyExpect).as("Expected key").isNotNull();
        assertThat(keyExpect).as("Expected key").isNotBlank();
        assertThat(keyActual).as("Actual key").isEqualTo(keyExpect);
    }

    /**
     * Validates string key equality with a custom failure description.
     *
     * @param keyActual   The actual value from response
     * @param keyExpect   The expected value
     * @param description Custom failure message
     */
    public void verifyStringKey(String keyActual, String keyExpect, String description) {
        assertThat(keyActual).as(description).isEqualTo(keyExpect);
    }

    /**
     * Validates that an Integer value is greater than a given threshold.
     *
     * @param actual      Actual int value
     * @param threshold   Value it should exceed
     * @param description Custom message
     */
    public void verifyIntegerGreaterThan(int actual, int threshold, String description) {
        assertThat(actual).as(description).isGreaterThan(threshold);
    }

    /**
     * Validates Integer equality with description.
     *
     * @param actual      Actual integer
     * @param expected    Expected integer
     * @param description Custom message
     */
    public void verifyIntegerKey(Integer actual, Integer expected, String description) {
        assertThat(actual).as(description).isEqualTo(expected);
    }

    /**
     * Validates Boolean equality with description.
     *
     * @param actual      Actual boolean
     * @param expected    Expected boolean
     * @param description Custom message
     */
    public void verifyBooleanKey(Boolean actual, Boolean expected, String description) {
        assertThat(actual).as(description).isEqualTo(expected);
    }

    /**
     * Validates that an Integer key (e.g., booking ID) is not null.
     *
     * @param keyExpect The integer to check
     */
    public void verifyStringKeyNotNull(Integer keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    /**
     * Overloaded method to validate that a String key is not null.
     *
     * @param keyExpect The string to check
     */
    public void verifyStringKeyNotNull(String keyExpect) {
        assertThat(keyExpect).isNotNull();
    }
}