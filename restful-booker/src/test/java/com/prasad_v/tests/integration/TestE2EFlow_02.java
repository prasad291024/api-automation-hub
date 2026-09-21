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
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test flow for:
 * 1. Creating a booking.
 * 2. Verifying that the booking exists.
 * 3. Deleting the booking.
 * 4. Verifying that the booking has been successfully deleted (returns HTTP 404).
 */
public class TestE2EFlow_02 extends BaseTest {

    /**
     * Step 1: Create a Booking
     */
    @Test(priority = 1)
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 1: Create a booking and store booking ID")
    public void createBooking(ITestContext context) {
        Booking bookingPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(120, 400))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(2))
                .withCheckout(DataGenerator.generateFutureDate(6))
                .withAdditionalneeds("Late Checkin")
                .build();

        BookingResponse response = bookingClient.createBooking(bookingPayload);
        BookingAssertions.verifyBookingIdValid(response.getBookingid());
        BookingAssertions.verifyBookingDetails(response.getBooking(), bookingPayload);

        context.setAttribute("bookingid", response.getBookingid());
        context.setAttribute("createdBooking", bookingPayload);
    }

    /**
     * Step 2: Verify Booking was Created
     */
    @Test(priority = 2, dependsOnMethods = "createBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 2: Verify booking by ID exists after creation")
    public void verifyBooking(ITestContext context) {
        Integer bookingId = (Integer) context.getAttribute("bookingid");
        Booking expected = (Booking) context.getAttribute("createdBooking");

        Booking retrieved = bookingClient.getBooking(bookingId);
        BookingAssertions.verifyBookingDetails(retrieved, expected);
    }

    /**
     * Step 3: Delete the Booking
     */
    @Test(priority = 3, dependsOnMethods = "verifyBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 3: Delete the booking using token")
    public void deleteBooking(ITestContext context) {
        Integer bookingId = (Integer) context.getAttribute("bookingid");
        boolean isDeleted = bookingClient.deleteBooking(bookingId);
        assertThat(isDeleted).as("Booking deletion should succeed").isTrue();
    }

    /**
     * Step 4: Verify Booking is Deleted
     */
    @Test(priority = 4, dependsOnMethods = "deleteBooking")
    @Owner("Prasad")
    @Description("TC#E2E2 - Step 4: Verify booking no longer exists")
    public void verifyBookingDeleted(ITestContext context) {
        Integer bookingId = (Integer) context.getAttribute("bookingid");
        Response response = bookingService.getBookingById(bookingId);
        BookingAssertions.verifyStatusCode(response, 404);
    }
}
