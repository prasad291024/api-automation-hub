package com.prasad_v.tests.clients;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Demonstrates high-level, business-focused API testing using BookingClient and AuthClient.
 * Tests operate directly on typed domain objects with automatic token injection and response deserialization.
 */
@Feature("Booking Management - Client Layer")
@Owner("Prasad")
public class BookingClientTests extends BaseTest {

    @Test(groups = {"smoke", "reg"}, priority = 1)
    @Story("Create and Read Booking")
    @Description("Verify creating and retrieving a booking using BookingClient abstraction")
    public void testCreateAndGetBookingViaClient() {
        // Step 1: Create dynamic test payload
        Booking bookingPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(150, 450))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(2))
                .withCheckout(DataGenerator.generateFutureDate(7))
                .withAdditionalneeds("Breakfast")
                .build();

        // Step 2: Create booking using client (handles HTTP, serialization & deserialization)
        BookingResponse created = bookingClient.createBooking(bookingPayload);
        BookingAssertions.verifyBookingIdValid(created.getBookingid());
        BookingAssertions.verifyBookingDetails(created.getBooking(), bookingPayload);

        // Step 3: Retrieve booking by ID
        Booking retrieved = bookingClient.getBooking(created.getBookingid());
        BookingAssertions.verifyBookingDetails(retrieved, bookingPayload);
    }

    @Test(groups = {"reg"}, priority = 2)
    @Story("Update Booking")
    @Description("Verify updating an existing booking with automatic token management")
    public void testUpdateBookingViaClient() {
        // Step 1: Create initial booking
        Booking initialPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(200)
                .withDepositpaid(false)
                .withCheckin(DataGenerator.generateFutureDate(3))
                .withCheckout(DataGenerator.generateFutureDate(6))
                .withAdditionalneeds("WiFi")
                .build();

        BookingResponse created = bookingClient.createBooking(initialPayload);
        int bookingId = created.getBookingid();

        // Step 2: Prepare updated data
        Booking updatedPayload = new BookingBuilder()
                .withFirstname(initialPayload.getFirstname())
                .withLastname("UpdatedLastname")
                .withTotalprice(350)
                .withDepositpaid(true)
                .withCheckin(initialPayload.getBookingdates().getCheckin())
                .withCheckout(initialPayload.getBookingdates().getCheckout())
                .withAdditionalneeds("Dinner & Drinks")
                .build();

        // Step 3: Update booking using client (token auto-injected by AuthClient)
        Booking updated = bookingClient.updateBooking(bookingId, updatedPayload);
        assertEquals(updated.getLastname(), "UpdatedLastname", "Lastname should be updated");
        assertEquals(updated.getTotalprice(), Integer.valueOf(350), "Totalprice should be updated");
        assertTrue(updated.getDepositpaid(), "Depositpaid should now be true");
        assertEquals(updated.getAdditionalneeds(), "Dinner & Drinks");
    }

    @Test(groups = {"reg"}, priority = 3)
    @Story("Delete Booking")
    @Description("Verify deleting a booking using BookingClient with automatic token management")
    public void testDeleteBookingViaClient() {
        // Step 1: Create booking to be deleted
        Booking payload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(180)
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(3))
                .withAdditionalneeds("Late Checkout")
                .build();

        BookingResponse created = bookingClient.createBooking(payload);

        // Step 2: Delete booking
        boolean isDeleted = bookingClient.deleteBooking(created.getBookingid());
        assertTrue(isDeleted, "Booking deletion should return HTTP 201 (success)");
    }

    @Test(groups = {"smoke"}, priority = 4)
    @Story("Health Check")
    @Description("Verify health check via BookingClient ping")
    public void testHealthCheckViaClient() {
        boolean healthy = bookingClient.isHealthy();
        assertTrue(healthy, "Booking API should be healthy and responding to ping");
    }
}
