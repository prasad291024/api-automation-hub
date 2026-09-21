package com.prasad_v.tests.crud;

import com.prasad_v.asserts.AssertActions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.modules.PayloadManager;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.services.BookingService;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Improved test class demonstrating better practices for API testing.
 * This refactored version properly uses framework capabilities and follows
 * improved testing patterns.
 */
public class TestCreateBookingImproved extends BaseTest {


    /**
     * Test Case ID: TC#INT1_IMPROVED
     * This test validates the creation of a new booking with improved practices.
     * It uses proper test data generation, service layer abstraction, and
     * comprehensive validation.
     */
    @Test(groups = {"reg", "smoke"}, priority = 1)
    @TmsLink("https://bugz.atlassian.net/browse/TS-1")
    @Owner("Prasad")
    @Description("TC#INT1_IMPROVED - Verify that a Booking can be Created with proper validation")
    public void testCreateBookingPOST_WithImprovedPractices() {
        // Step: Generate dynamic test data
        Booking testData = generateValidBookingData();

        // Step: Create booking using service layer
        int bookingId = createBookingAndGetId(testData);

        // Step: Verify booking was created successfully
        verifyBookingCreation(bookingId, testData);

        // Step: Retrieve and validate the created booking
        verifyBookingRetrieval(bookingId, testData);
    }

    /**
     * Data-driven test to validate booking creation with various data sets.
     * Demonstrates use of TestNG DataProvider for varied test scenarios.
     */
    @Test(groups = {"reg"}, dataProvider = "bookingTestData")
    @TmsLink("https://bugz.atlassian.net/browse/TS-2")
    @Owner("QA_Team")
    @Description("Verify booking creation with various valid data combinations")
    public void testCreateBookingPOST_DataDriven(Map<String, Object> testData) {
        Booking booking = convertToBooking(testData);
        int bookingId = createBookingAndGetId(booking);
        verifyBookingCreation(bookingId, booking);
    }

    /**
     * Provides test data for data-driven booking creation tests.
     * Includes boundary values, typical values, and edge cases.
     */
    @DataProvider(name = "bookingTestData")
    public Object[][] bookingTestData() {
        return new Object[][]{
            {
                Map.of(
                    "firstname", "John",
                    "lastname", "Doe",
                    "totalprice", 100,
                    "depositpaid", true,
                    "checkin", "2024-03-01",
                    "checkout", "2024-03-05",
                    "additionalneeds", "Breakfast"
                )
            },
            {
                Map.of(
                    "firstname", "Jane",
                    "lastname", "Smith",
                    "totalprice", 999,
                    "depositpaid", false,
                    "checkin", "2024-02-15",
                    "checkout", "2024-02-20",
                    "additionalneeds", ""
                )
            },
            {
                Map.of(
                    "firstname", DataGenerator.generateRandomString(5),
                    "lastname", DataGenerator.generateRandomString(7),
                    "totalprice", DataGenerator.generateRandomInt(50, 500),
                    "depositpaid", DataGenerator.generateRandomBoolean(),
                    "checkin", DataGenerator.generateFutureDate(1),
                    "checkout", DataGenerator.generateFutureDate(5),
                    "additionalneeds", DataGenerator.generateRandomStringFromArray(
                        "Breakfast", "Lunch", "Dinner", "None"
                    )
                )
            }
        };
    }

    // ============================================================================
    // PRIVATE HELPER METHODS - Encapsulate test steps for clarity and reuse
    // ============================================================================

    @Step("Generate valid booking test data")
    private Booking generateValidBookingData() {
        return new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomString(6))
                .withLastname(DataGenerator.generateRandomString(8))
                .withTotalprice(DataGenerator.generateRandomInt(50, 500))
                .withDepositpaid(DataGenerator.generateRandomBoolean())
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(5))
                .withAdditionalneeds(DataGenerator.generateRandomStringFromArray(
                        "Breakfast", "Lunch", "Dinner", "None"
                ))
                .build();
    }

    @Step("Create booking and return booking ID")
    private int createBookingAndGetId(Booking bookingData) {
        // Use service layer for API interaction
        Response response = bookingService.createBooking(bookingData);

        // Validate response status code
        response.then().log().all().statusCode(200);

        // Extract booking ID from response
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        return bookingResponse.getBookingid();
    }

    @Step("Verify booking creation response")
    private void verifyBookingCreation(int bookingId, Booking expectedData) {
        // Additional validation could go here (response time, headers, etc.)
        assertActions.verifyIntegerGreaterThan(bookingId, 0,
            "Booking ID should be positive");
    }

    @Step("Retrieve and verify created booking")
    private void verifyBookingRetrieval(int bookingId, Booking expectedData) {
        // Get the created booking
        Response getResponse = bookingService.getBookingById(bookingId);
        getResponse.then().log().all().statusCode(200);

        // Convert response to Booking object
        Booking actualBooking = payloadManager.getResponseFromJSON(getResponse.asString());

        // Validate all fields match
        assertActions.verifyStringKey(actualBooking.getFirstname(), expectedData.getFirstname(),
            "Firstname should match");
        assertActions.verifyStringKey(actualBooking.getLastname(), expectedData.getLastname(),
            "Lastname should match");
        assertActions.verifyIntegerKey(actualBooking.getTotalprice(), expectedData.getTotalprice(),
            "Total price should match");
        assertActions.verifyBooleanKey(actualBooking.getDepositpaid(), expectedData.getDepositpaid(),
            "Deposit paid should match");
        assertActions.verifyStringKey(actualBooking.getAdditionalneeds(), expectedData.getAdditionalneeds(),
            "Additional needs should match");

        // Validate booking dates if present
        if (expectedData.getBookingdates() != null && actualBooking.getBookingdates() != null) {
            assertActions.verifyStringKey(
                actualBooking.getBookingdates().getCheckin(),
                expectedData.getBookingdates().getCheckin(),
                "Checkin date should match"
            );
            assertActions.verifyStringKey(
                actualBooking.getBookingdates().getCheckout(),
                expectedData.getBookingdates().getCheckout(),
                "Checkout date should match"
            );
        }
    }

    @Step("Convert map data to Booking object")
    private Booking convertToBooking(Map<String, Object> dataMap) {
        BookingBuilder builder = new BookingBuilder();

        if (dataMap.containsKey("firstname")) {
            builder.withFirstname((String) dataMap.get("firstname"));
        }
        if (dataMap.containsKey("lastname")) {
            builder.withLastname((String) dataMap.get("lastname"));
        }
        if (dataMap.containsKey("totalprice")) {
            builder.withTotalprice((Integer) dataMap.get("totalprice"));
        }
        if (dataMap.containsKey("depositpaid")) {
            builder.withDepositpaid((Boolean) dataMap.get("depositpaid"));
        }
        if (dataMap.containsKey("checkin")) {
            builder.withCheckin((String) dataMap.get("checkin"));
        }
        if (dataMap.containsKey("checkout")) {
            builder.withCheckout((String) dataMap.get("checkout"));
        }
        if (dataMap.containsKey("additionalneeds")) {
            builder.withAdditionalneeds((String) dataMap.get("additionalneeds"));
        }

        return builder.build();
    }
}