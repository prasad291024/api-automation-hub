package com.prasad_v.utils;

import com.github.javafaker.Faker;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating test data for API tests.
 * Provides methods for creating realistic and varied test data
 * to improve test coverage and reduce brittleness.
 */
public class DataGenerator {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DataGenerator() {
        // Allow instantiation for test use
    }

    /**
     * Generate a random string of specified length using alphabetic characters.
     *
     * @param length Length of the string to generate
     * @return Random string
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomChar = random.nextInt(26) + 'a';
            sb.append((char) randomChar);
        }
        return sb.toString();
    }

    /**
     * Generate a random integer within the specified range (inclusive).
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random integer
     */
    public static int generateRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Generate a random boolean value.
     *
     * @return Random boolean
     */
    public static boolean generateRandomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Generate a random string from a predefined array of options.
     *
     * @param options Array of string options
     * @return Randomly selected option
     */
    public static String generateRandomStringFromArray(String... options) {
        if (options == null || options.length == 0) {
            return "";
        }
        return options[random.nextInt(options.length)];
    }

    /**
     * Generate a future date string in yyyy-MM-dd format.
     *
     * @param daysAhead Number of days in the future (must be positive)
     * @return Future date string
     */
    public static String generateFutureDate(int daysAhead) {
        if (daysAhead < 0) {
            throw new IllegalArgumentException("Days ahead must be positive");
        }
        LocalDate futureDate = LocalDate.now().plusDays(daysAhead);
        return futureDate.format(DATE_FORMATTER);
    }

    /**
     * Generate a past date string in yyyy-MM-dd format.
     *
     * @param daysAgo Number of days in the past (must be positive)
     * @return Past date string
     */
    public static String generatePastDate(int daysAgo) {
        if (daysAgo < 0) {
            throw new IllegalArgumentException("Days ago must be positive");
        }
        LocalDate pastDate = LocalDate.now().minusDays(daysAgo);
        return pastDate.format(DATE_FORMATTER);
    }

    /**
     * Generate a random email address.
     *
     * @return Random email address
     */
    public static String generateRandomEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * Generate a random phone number.
     *
     * @return Random phone number
     */
    public static String generateRandomPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    /**
     * Generate a random name.
     *
     * @return Random name
     */
    public static String generateRandomName() {
        return faker.name().fullName();
    }

    /**
     * Generate a random first name.
     *
     * @return Random first name
     */
    public static String generateRandomFirstName() {
        return faker.name().firstName();
    }

    /**
     * Generate a random last name.
     *
     * @return Random last name
     */
    public static String generateRandomLastName() {
        return faker.name().lastName();
    }

    /**
     * Generate a random city name.
     *
     * @return Random city name
     */
    public static String generateRandomCity() {
        return faker.address().city();
    }

    /**
     * Generate a random street address.
     *
     * @return Random street address
     */
    public static String generateRandomStreetAddress() {
        return faker.address().streetAddress();
    }

    /**
     * Generate a random zip/postal code.
     *
     * @return Random zip code
     */
    public static String generateRandomZipCode() {
        return faker.address().zipCode();
    }

    /**
     * Generate a random country.
     *
     * @return Random country
     */
    public static String generateRandomCountry() {
        return faker.address().country();
    }

    /**
     * Generate a pair of future booking dates [checkin, checkout] formatted as yyyy-MM-dd.
     * Guarantees that checkout is after checkin.
     *
     * @param stayDays duration of stay in days (must be at least 1)
     * @return String array of size 2 containing [checkin, checkout]
     */
    public static String[] generateBookingDates(int stayDays) {
        int checkinOffset = generateRandomInt(1, 30);
        int stay = Math.max(1, stayDays);
        LocalDate checkin = LocalDate.now().plusDays(checkinOffset);
        LocalDate checkout = checkin.plusDays(stay);
        return new String[]{checkin.format(DATE_FORMATTER), checkout.format(DATE_FORMATTER)};
    }

    /**
     * Generate a random monetary price between min and max.
     *
     * @param min minimum price
     * @param max maximum price
     * @return randomized price
     */
    public static int generatePrice(int min, int max) {
        return generateRandomInt(min, max);
    }

    /**
     * Generate a random username.
     *
     * @return random username
     */
    public static String generateUsername() {
        return faker.name().username();
    }

    /**
     * Generate a random password.
     *
     * @param minLength minimum password length
     * @param maxLength maximum password length
     * @return random password
     */
    public static String generatePassword(int minLength, int maxLength) {
        return faker.internet().password(minLength, maxLength, true, true);
    }

    /**
     * Generate a random UUID string.
     *
     * @return UUID string
     */
    public static String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Generate an alphanumeric string of specified length.
     *
     * @param length length of string
     * @return alphanumeric string
     */
    public static String generateAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}