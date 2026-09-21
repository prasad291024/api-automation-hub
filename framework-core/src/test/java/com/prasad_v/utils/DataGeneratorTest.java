package com.prasad_v.utils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

public class DataGeneratorTest {

    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void testGenerateFirstNameAndLastName() {
        String firstName = DataGenerator.generateRandomFirstName();
        String lastName = DataGenerator.generateRandomLastName();

        Assert.assertNotNull(firstName);
        Assert.assertFalse(firstName.trim().isEmpty());
        Assert.assertNotNull(lastName);
        Assert.assertFalse(lastName.trim().isEmpty());
    }

    @Test
    public void testGeneratePrice() {
        int price = DataGenerator.generatePrice(50, 1000);
        Assert.assertTrue(price >= 50 && price <= 1000);
    }

    @Test
    public void testGenerateBookingDates() {
        String[] dates = DataGenerator.generateBookingDates(5);
        Assert.assertNotNull(dates);
        Assert.assertEquals(dates.length, 2);

        LocalDate checkin = LocalDate.parse(dates[0]);
        LocalDate checkout = LocalDate.parse(dates[1]);
        Assert.assertTrue(checkout.isAfter(checkin));
    }

    @Test
    public void testGenerateUuidAndAlphanumeric() {
        String uuid = DataGenerator.generateUuid();
        Assert.assertNotNull(uuid);
        Assert.assertEquals(uuid.length(), 36);

        String alphanumeric = DataGenerator.generateAlphanumeric(12);
        Assert.assertNotNull(alphanumeric);
        Assert.assertEquals(alphanumeric.length(), 12);
    }
}
