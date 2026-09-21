package com.prasad_v.tests.contract;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * Contract tests to ensure Restful-Booker API responses adhere to predefined JSON schemas.
 * Guards against payload format changes, unexpected nulls, and type deviations.
 */
@Epic("Restful Booker Contract Tests")
@Feature("JSON Schema Contract Validation")
@Owner("Prasad")
public class BookingContractTests extends BaseTest {

    @Test(groups = {"contract", "smoke"}, priority = 1)
    @Description("Verify POST /booking response matches booking-response-schema.json")
    public void testCreateBookingContract() {
        Booking payload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(100, 500))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(2))
                .withCheckout(DataGenerator.generateFutureDate(5))
                .withAdditionalneeds("Breakfast")
                .build();

        Response response = bookingService.createBooking(payloadManager.createPayloadBookingAsString(payload));
        BookingAssertions.verifyStatusCode(response, 200);
        BookingAssertions.verifySchema(response, "booking-response-schema.json");
    }

    @Test(groups = {"contract"}, priority = 2)
    @Description("Verify GET /booking/{id} response matches booking-schema.json")
    public void testGetBookingByIdContract() {
        Booking payload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(250)
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(3))
                .withAdditionalneeds("None")
                .build();

        Response createResponse = bookingService.createBooking(payloadManager.createPayloadBookingAsString(payload));
        int bookingId = payloadManager.bookingResponseJava(createResponse.asString()).getBookingid();

        Response getResponse = bookingService.getBookingById(bookingId);
        BookingAssertions.verifyStatusCode(getResponse, 200);
        BookingAssertions.verifySchema(getResponse, "booking-schema.json");
    }

    @Test(groups = {"contract", "smoke"}, priority = 3)
    @Description("Verify POST /auth response matches auth-token-schema.json")
    public void testAuthTokenContract() {
        Response response = authService.createTokenResponse(payloadManager.setAuthPayload());
        BookingAssertions.verifyStatusCode(response, 200);
        BookingAssertions.verifySchema(response, "auth-token-schema.json");
    }
}
