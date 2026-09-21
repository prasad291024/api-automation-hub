package com.prasad_v.asserts;

import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.pojos.Bookingdates;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Domain-specific assertion library for Booking API tests.
 * Integrates with AssertJ for clear error messages and Allure for detailed step reporting.
 */
public class BookingAssertions {

    @Step("Verify status code is {expectedStatusCode}")
    public static void verifyStatusCode(Response response, int expectedStatusCode) {
        assertThat(response).as("Response object").isNotNull();
        assertThat(response.getStatusCode())
                .as("Status code check")
                .isEqualTo(expectedStatusCode);
    }

    @Step("Verify booking ID is generated and positive: {bookingId}")
    public static void verifyBookingIdValid(Integer bookingId) {
        assertThat(bookingId)
                .as("Booking ID should not be null and must be positive")
                .isNotNull()
                .isPositive();
    }

    @Step("Verify created booking response matches expected payload")
    public static void verifyBookingResponse(BookingResponse response, Booking expected) {
        assertThat(response).as("BookingResponse").isNotNull();
        verifyBookingIdValid(response.getBookingid());
        verifyBookingDetails(response.getBooking(), expected);
    }

    @Step("Verify booking details match expected")
    public static void verifyBookingDetails(Booking actual, Booking expected) {
        assertThat(actual).as("Actual Booking").isNotNull();
        assertThat(expected).as("Expected Booking").isNotNull();

        assertThat(actual.getFirstname())
                .as("Firstname should match")
                .isEqualTo(expected.getFirstname());

        assertThat(actual.getLastname())
                .as("Lastname should match")
                .isEqualTo(expected.getLastname());

        assertThat(actual.getTotalprice())
                .as("TotalPrice should match")
                .isEqualTo(expected.getTotalprice());

        assertThat(actual.getDepositpaid())
                .as("DepositPaid flag should match")
                .isEqualTo(expected.getDepositpaid());

        verifyBookingDates(actual.getBookingdates(), expected.getBookingdates());

        if (expected.getAdditionalneeds() != null) {
            assertThat(actual.getAdditionalneeds())
                    .as("Additional needs should match")
                    .isEqualTo(expected.getAdditionalneeds());
        }
    }

    @Step("Verify booking dates (checkin & checkout)")
    public static void verifyBookingDates(Bookingdates actual, Bookingdates expected) {
        if (expected == null) return;
        assertThat(actual).as("Bookingdates").isNotNull();

        assertThat(actual.getCheckin())
                .as("Checkin date should match")
                .isEqualTo(expected.getCheckin());

        assertThat(actual.getCheckout())
                .as("Checkout date should match")
                .isEqualTo(expected.getCheckout());
    }

    @Step("Verify response conforms to JSON schema: {schemaFileName}")
    public static void verifySchema(Response response, String schemaFileName) {
        com.prasad_v.validation.SchemaValidator.assertSchema(response, schemaFileName);
    }
}
