package com.prasad_v.tests.crud;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

/**
 * Automates the creation of a booking using the service layer and dynamic test data.
 */
public class TestCreateBooking extends BaseTest {

    /**
     * Test Case ID: TC#INT1
     * Validates booking creation using BookingService, dynamic DataGenerator, and BookingAssertions.
     */
    @Test(groups = {"reg", "smoke"}, priority = 1)
    @TmsLink("https://bugz.atlassian.net/browse/TS-1")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 1. Verify that the Booking can be Created")
    public void testCreateBookingPOST() {
        // Step 1: Generate dynamic booking data
        Booking bookingPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(100, 500))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(5))
                .withAdditionalneeds("Breakfast")
                .build();

        // Step 2: Send POST request via BookingService
        response = bookingService.createBooking(payloadManager.createPayloadBookingAsString(bookingPayload));

        // Step 3: Validate status code
        BookingAssertions.verifyStatusCode(response, 200);

        // Step 4: Deserialize and validate full booking response against generated payload
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        BookingAssertions.verifyBookingResponse(bookingResponse, bookingPayload);
    }
}
