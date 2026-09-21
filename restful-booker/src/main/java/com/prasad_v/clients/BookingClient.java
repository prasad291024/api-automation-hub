package com.prasad_v.clients;

import com.prasad_v.modules.PayloadManager;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.BookingResponse;
import com.prasad_v.services.BookingService;
import io.restassured.response.Response;

/**
 * Domain-specific client abstraction for Booking operations.
 * Handles serialization, token injection, and typed object returns.
 */
public class BookingClient {

    private final BookingService bookingService;
    private final AuthClient authClient;
    private final PayloadManager payloadManager;

    public BookingClient() {
        this.bookingService = new BookingService();
        this.authClient = new AuthClient();
        this.payloadManager = new PayloadManager();
    }

    public BookingClient(BookingService bookingService, AuthClient authClient) {
        this.bookingService = bookingService;
        this.authClient = authClient;
        this.payloadManager = new PayloadManager();
    }

    /**
     * Creates a new booking and returns the typed BookingResponse.
     *
     * @param payload Booking details
     * @return BookingResponse containing the generated booking ID and details
     */
    public BookingResponse createBooking(Booking payload) {
        String jsonPayload = payloadManager.createPayloadBookingAsString(payload);
        Response response = bookingService.createBooking(jsonPayload);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to create booking. Status: " + response.getStatusCode()
                    + ", Body: " + response.asString());
        }
        return payloadManager.bookingResponseJava(response.asString());
    }

    /**
     * Retrieves a booking by its ID and returns the typed Booking model.
     *
     * @param bookingId ID of the booking to retrieve
     * @return Booking details
     */
    public Booking getBooking(int bookingId) {
        Response response = bookingService.getBookingById(bookingId);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to retrieve booking " + bookingId + ". Status: "
                    + response.getStatusCode());
        }
        return payloadManager.getResponseFromJSON(response.asString());
    }

    /**
     * Updates an existing booking, automatically obtaining and attaching an authentication token.
     *
     * @param bookingId ID of the booking to update
     * @param updatedPayload Updated booking details
     * @return Updated Booking details
     */
    public Booking updateBooking(int bookingId, Booking updatedPayload) {
        String token = authClient.getOrCreateToken();
        String jsonPayload = payloadManager.createPayloadBookingAsString(updatedPayload);
        Response response = bookingService.updateBooking(bookingId, jsonPayload, token);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to update booking " + bookingId + ". Status: "
                    + response.getStatusCode() + ", Body: " + response.asString());
        }
        return payloadManager.getResponseFromJSON(response.asString());
    }

    /**
     * Deletes a booking, automatically obtaining and attaching an authentication token.
     *
     * @param bookingId ID of the booking to delete
     * @return true if successfully deleted (HTTP 201 Created per Restful-Booker spec)
     */
    public boolean deleteBooking(int bookingId) {
        String token = authClient.getOrCreateToken();
        Response response = bookingService.deleteBooking(bookingId, token);
        return response.getStatusCode() == 201;
    }

    /**
     * Checks if the Booking API ping endpoint is healthy.
     *
     * @return true if ping succeeds
     */
    public boolean isHealthy() {
        Response response = bookingService.ping();
        return response.getStatusCode() == 201;
    }

    public BookingService getService() {
        return bookingService;
    }

    public AuthClient getAuthClient() {
        return authClient;
    }
}
