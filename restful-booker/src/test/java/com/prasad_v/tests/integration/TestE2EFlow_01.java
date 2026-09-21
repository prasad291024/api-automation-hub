package com.prasad_v.tests.integration;

import com.prasad_v.asserts.BookingAssertions;
import com.prasad_v.builders.BookingBuilder;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.tests.base.BaseTest;
import com.prasad_v.utils.DataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End (E2E) integration test flow for booking operations using BookingClient.
 *
 * Steps:
 * 1. Create a Booking with dynamic data -> generate booking ID.
 * 2. Verify the created booking details via GET request.
 * 3. Update the booking details via PUT request (with automated auth token handling).
 * 4. Delete the booking by ID.
 */
public class TestE2EFlow_01 extends BaseTest {

    /**
     * Step 1: Create a Booking and Store Booking ID
     */
    @Test(groups = {"qa", "reg"}, priority = 1)
    @Owner("Prasad")
    @Description("TC#INT1 - Step 1. Verify that the Booking can be Created")
    public void testCreateBooking(ITestContext iTestContext) {
        Booking bookingPayload = new BookingBuilder()
                .withFirstname(DataGenerator.generateRandomFirstName())
                .withLastname(DataGenerator.generateRandomLastName())
                .withTotalprice(DataGenerator.generatePrice(100, 500))
                .withDepositpaid(true)
                .withCheckin(DataGenerator.generateFutureDate(1))
                .withCheckout(DataGenerator.generateFutureDate(5))
                .withAdditionalneeds("Breakfast")
                .build();

        BookingResponse bookingResponse = bookingClient.createBooking(bookingPayload);
        BookingAssertions.verifyBookingIdValid(bookingResponse.getBookingid());
        BookingAssertions.verifyBookingDetails(bookingResponse.getBooking(), bookingPayload);

        // Store context attributes for dependent steps
        iTestContext.setAttribute("bookingid", bookingResponse.getBookingid());
        iTestContext.setAttribute("createdBooking", bookingPayload);
    }

    /**
     * Step 2: Verify the Booking by Booking ID
     */
    @Test(groups = {"qa", "reg"}, priority = 2, dependsOnMethods = "testCreateBooking")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 2. Verify the Booking By ID")
    public void testVerifyBookingId(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        Booking expected = (Booking) iTestContext.getAttribute("createdBooking");

        Booking retrievedBooking = bookingClient.getBooking(bookingid);
        BookingAssertions.verifyBookingDetails(retrievedBooking, expected);
    }

    /**
     * Step 3: Update the Booking
     */
    @Test(groups = {"qa", "reg"}, priority = 3, dependsOnMethods = "testVerifyBookingId")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 3. Verify Updated Booking by ID")
    public void testUpdateBookingByID(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        Booking current = (Booking) iTestContext.getAttribute("createdBooking");

        Booking updatePayload = new BookingBuilder()
                .withFirstname(current.getFirstname())
                .withLastname("UpdatedLastname")
                .withTotalprice(current.getTotalprice() + 50)
                .withDepositpaid(true)
                .withCheckin(current.getBookingdates().getCheckin())
                .withCheckout(current.getBookingdates().getCheckout())
                .withAdditionalneeds("Dinner")
                .build();

        Booking updated = bookingClient.updateBooking(bookingid, updatePayload);
        BookingAssertions.verifyBookingDetails(updated, updatePayload);
    }

    /**
     * Step 4: Delete the Booking
     */
    @Test(groups = {"qa", "reg"}, priority = 4, dependsOnMethods = "testUpdateBookingByID")
    @Owner("Prasad")
    @Description("TC#INT1 - Step 4. Delete the Booking by ID")
    public void testDeleteBookingById(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        boolean isDeleted = bookingClient.deleteBooking(bookingid);
        assertThat(isDeleted).as("Booking should be deleted successfully").isTrue();
    }
}
