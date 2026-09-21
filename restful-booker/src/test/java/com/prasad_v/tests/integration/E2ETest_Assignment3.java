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
 * Assignment 3: Create Booking → Update it → Try to Delete it.
 * Validates full lifecycle using BookingClient and assertions.
 */
public class E2ETest_Assignment3 extends BaseTest {

    @Test(groups = {"reg", "e2e"}, priority = 3)
    @Owner("Prasad")
    @Description("Assignment 3: Create Booking -> Update it -> Delete it -> Verify 404")
    public void testCreateUpdateDeleteBooking() {
        // Step 1: Create booking
        Booking createPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(150, 350))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(2))
                .withCheckout(DataGenerator.generateFutureDate(6))
                .withAdditionalneeds("Breakfast")
                .build();

        BookingResponse createResponse = bookingClient.createBooking(createPayload);
        int bookingId = createResponse.getBookingid();
        BookingAssertions.verifyBookingIdValid(bookingId);

        // Step 2: Update booking (token auto-managed by client)
        Booking updatePayload = new BookingBuilder()
                .withFirstname(createPayload.getFirstname())
                .withLastname("UpdatedAssignment3")
                .withTotalprice(createPayload.getTotalprice() + 50)
                .withDepositpaid(false)
                .withCheckin(createPayload.getBookingdates().getCheckin())
                .withCheckout(createPayload.getBookingdates().getCheckout())
                .withAdditionalneeds("Lunch & Dinner")
                .build();

        Booking updated = bookingClient.updateBooking(bookingId, updatePayload);
        BookingAssertions.verifyBookingDetails(updated, updatePayload);

        // Step 3: Delete booking
        boolean isDeleted = bookingClient.deleteBooking(bookingId);
        Assert.assertTrue(isDeleted, "Booking should be deleted successfully");

        // Step 4: Verify deleted booking returns 404
        Response getResponse = bookingService.getBookingById(bookingId);
        BookingAssertions.verifyStatusCode(getResponse, 404);
    }
}