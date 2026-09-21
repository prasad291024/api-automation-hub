package com.prasad_v.tests.integration;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Assignment 2: Get a Booking from Get All → Try to Delete it without auth.
 * Steps:
 * 1. Fetch all bookings (GET /booking) and get one bookingid.
 * 2. Try deleting that booking without authentication.
 * 3. Validate that deletion fails with 403 (Forbidden).
 */
public class E2ETest_Assignment2 extends BaseTest {

    @Test(groups = {"reg", "e2e"}, priority = 2)
    @Owner("Prasad")
    @Description("Assignment 2: Fetch all bookings and verify unauthenticated delete is rejected with 403 Forbidden")
    public void testGetAndTryToDeleteBooking() {
        // Step 1: Fetch all bookings via BookingService
        Response allBookingsResponse = bookingService.getAllBookings();
        BookingAssertions.verifyStatusCode(allBookingsResponse, 200);

        int bookingId = allBookingsResponse.jsonPath().getInt("[0].bookingid");
        Assert.assertTrue(bookingId > 0, "Booking ID should be greater than 0");

        // Step 2: Try to delete the booking WITHOUT authentication
        Response deleteResponse = bookingService.deleteBookingWithoutToken(bookingId);

        // Step 3: Expect HTTP 403 Forbidden
        BookingAssertions.verifyStatusCode(deleteResponse, 403);
    }
}