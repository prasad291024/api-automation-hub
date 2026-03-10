package com.prasad_v.tests.integration;

/*
    Delete a Booking → Try to Update it
    Steps:
        ✔ Create a new booking and get bookingid.
        ✔ Delete that booking.
        ✔ Try to update the deleted booking.
        ✔ Validate update fails with 405 (Method Not Allowed) or 404 (Not Found).
*/

import com.prasad_v.tests.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.*;

public class E2ETest_Assignment4 extends BaseTest {

    public int createBooking() {
        String requestBody = "{ \"firstname\": \"John\", \"lastname\": \"Doe\", \"totalprice\": 150, \"depositpaid\": true, \"bookingdates\": { \"checkin\": \"2025-03-25\", \"checkout\": \"2025-03-30\" }, \"additionalneeds\": \"Breakfast\" }";

        Response response = given()
                .spec(requestSpecification)
                .basePath("/booking")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post();

        response.then().log().all();
        Assert.assertEquals(response.statusCode(), 200);

        return response.jsonPath().getInt("bookingid");
    }

    @Test
    public void testDeleteThenTryToUpdateBooking() {
        int bookingId = createBooking();
        String token = getToken();

        // Delete the booking
        given()
                .spec(requestSpecification)
                .basePath("/booking/" + bookingId)
                .header("Cookie", "token=" + token)
                .when()
                .delete()
                .then()
                .log().all()
                .statusCode(201);

        // Try to update the deleted booking
        String updateRequestBody = "{ \"firstname\": \"James\", \"lastname\": \"Bond\" }";

        given()
                .spec(requestSpecification)
                .basePath("/booking/" + bookingId)
                .header("Cookie", "token=" + token)
                .contentType(ContentType.JSON)
                .body(updateRequestBody)
                .when()
                .put()
                .then()
                .log().all()
                .statusCode(anyOf(equalTo(400), equalTo(405)));
    }
}