package com.prasad_v.tests.integration;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assignment 4: Delete a Booking → Try to Update it.
 * Validates that updating a deleted booking is rejected (HTTP 405 Method Not Allowed or 404 Not Found).
 */
public class E2ETest_Assignment4 extends BaseTest {

    @Test(groups = {"reg", "e2e"}, priority = 4)
    @Owner("Prasad")
    @Description("Assignment 4: Delete booking and verify subsequent update attempt fails with 405 or 404")
    public void testDeleteThenTryToUpdateBooking() {
        // Step 1: Create booking
        Booking createPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(120, 280))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(3))
                .withAdditionalneeds("Early Checkin")
                .build();

        BookingResponse response = bookingClient.createBooking(createPayload);
        int bookingId = response.getBookingid();
        BookingAssertions.verifyBookingIdValid(bookingId);

        // Step 2: Delete booking
        boolean isDeleted = bookingClient.deleteBooking(bookingId);
        Assert.assertTrue(isDeleted, "Booking deletion should succeed");

        // Step 3: Try to update the deleted booking
        String token = authClient.getOrCreateToken();
        Booking updatePayload = new BookingBuilder()
                .withFirstname("James")
                .withLastname("Bond")
                .withTotalprice(300)
                .withDepositpaid(false)
                .withCheckin(createPayload.getBookingdates().getCheckin())
                .withCheckout(createPayload.getBookingdates().getCheckout())
                .withAdditionalneeds("Secret Mission")
                .build();

        Response updateResponse = bookingService.updateBooking(bookingId,
                payloadManager.createPayloadBookingAsString(updatePayload), token);

        // Step 4: Validate update fails with 405 Method Not Allowed or 404 Not Found
        int statusCode = updateResponse.getStatusCode();
        assertThat(statusCode)
                .as("Updating a deleted booking should fail with 405 or 404")
                .isIn(405, 404);
    }
}