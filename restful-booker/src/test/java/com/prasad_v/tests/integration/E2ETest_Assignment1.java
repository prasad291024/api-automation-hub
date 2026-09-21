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

/**
 * Assignment 1: Create Booking → Delete it → Verify it's deleted.
 * Uses BookingClient for high-level operations and BookingAssertions for verification.
 */
public class E2ETest_Assignment1 extends BaseTest {

    @Test(groups = {"reg", "e2e"}, priority = 1)
    @Owner("Prasad")
    @Description("Assignment 1: Create Booking -> Delete it -> Verify it's deleted (404)")
    public void testCreateDeleteVerifyBooking() {
        // Step 1: Create booking using client with dynamic data
        Booking payload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(100, 300))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(4))
                .withAdditionalneeds("Breakfast")
                .build();

        BookingResponse response = bookingClient.createBooking(payload);
        int bookingId = response.getBookingid();
        BookingAssertions.verifyBookingIdValid(bookingId);

        // Step 2: Delete booking using client (automatic token injection)
        boolean isDeleted = bookingClient.deleteBooking(bookingId);
        Assert.assertTrue(isDeleted, "Booking should be deleted successfully (HTTP 201)");

        // Step 3: Verify the booking is no longer accessible (404 Not Found)
        Response getResponse = bookingService.getBookingById(bookingId);
        BookingAssertions.verifyStatusCode(getResponse, 404);
    }
}