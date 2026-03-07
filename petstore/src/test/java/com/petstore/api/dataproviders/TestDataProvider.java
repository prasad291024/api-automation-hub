package com.petstore.api.dataproviders;

import com.petstore.api.models.Category;
import com.petstore.api.models.Pet;
import com.petstore.api.models.Tag;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.Collections;

import static com.petstore.api.config.Config.*;

/**
 * Test Data Provider Class
 * <p>
 * Provides test data for data-driven testing using TestNG DataProvider.
 * Contains multiple data sets for different test scenarios.
 *Covers valid, minimal, edge case, and negative scenarios for Petstore API.
 *
 * @author Prasad_V
 * @version 1.0
 */
public class TestDataProvider {

    /**
     *  Provides valid pet data for creating pets with complete information
        @return Object[][] containing Pet objects with valid complete data
        Used for happy-path testing of Create Pet API
     */

    @DataProvider(name = "validPetData")
    public Object[][] getValidPetData() {
        return new Object[][]{
                {createPet(10001L, "Dogs", "Buddy", STATUS_AVAILABLE, "friendly")},
                {createPet(10002L, "Dogs", "Max", STATUS_PENDING, "playful")},
                {createPet(10003L, "Cats", "Whiskers", STATUS_SOLD, "calm")},
                {createPet(10004L, "Cats", "Mittens", STATUS_AVAILABLE, "active")},
                {createPet(10005L, "Birds", "Tweety", STATUS_PENDING, "colorful")}
        };
    }

    /**
     * Provides pet data with minimum required fields only
     *
     * @return Object[][] containing Pet objects with minimal data
     * Used to validate API behavior with partial payloads
     */

    @DataProvider(name = "minimalPetData")
    public Object[][] getMinimalPetData() {
        return new Object[][]{
                {Pet.builder()
                        .name("MinimalPet1")
                        .photoUrls(Collections.singletonList("https://example.com/photo1.jpg"))
                        .build()},
                {Pet.builder()
                        .name("MinimalPet2")
                        .photoUrls(Collections.singletonList("https://example.com/photo2.jpg"))
                        .build()}
        };
    }

    /**
     * Provides pet data with special characters in names (edge cases)
     * @return Object[][] containing Pet objects with special character names
     * Edge case pet names with special characters, spaces, and long strings
     * Used to test boundary conditions and input validation
     */

    @DataProvider(name = "specialCharacterPetData")
    public Object[][] getSpecialCharacterPetData() {
        return new Object[][]{
                {createPet(30001L, "Special", "Pet@123!#$%", STATUS_AVAILABLE, "special")},
                {createPet(30002L, "Special", "A", STATUS_PENDING, "short")},
                {createPet(30003L, "Special", "VeryLongNameWithMoreThan50CharactersToTestBoundaryLimits",
                        STATUS_SOLD, "long")},
                {createPet(30004L, "Special", "Pet With Spaces", STATUS_AVAILABLE, "spaces")},
                {createPet(30005L, "Special", "Pet-With-Dashes", STATUS_PENDING, "dashes")}
        };
    }

    /**
     * Provides valid pet IDs for GET/DELETE operations
     *
     * @return Object[][] containing valid pet IDs
     * Valid pet IDs for GET and DELETE operations
     */

    @DataProvider(name = "validPetIds")
    public Object[][] getValidPetIds() {
        return new Object[][]{
                {10001L},
                {10002L},
                {10003L}
        };
    }

    /**
     * Provides invalid pet IDs for negative testing
     *
     * @return Object[][] containing invalid pet IDs
     * Invalid pet IDs for negative testing
     * Includes non-existent, negative, and zero values
     */

    @DataProvider(name = "invalidPetIds")
    public Object[][] getInvalidPetIds() {
        return new Object[][]{
                {999999999L},  // Non-existent ID
                {-1L},         // Negative ID
                {0L}           // Zero ID
        };
    }

    /**
     * Provides valid status values for findByStatus endpoint
     *
     * @return Object[][] containing valid status values
     * Valid status values for findByStatus endpoint
     */

    @DataProvider(name = "validStatusValues")
    public Object[][] getValidStatusValues() {
        return new Object[][]{
                {STATUS_AVAILABLE},
                {STATUS_PENDING},
                {STATUS_SOLD}
        };
    }

    /**
     * Provides invalid status values for negative testing
     *
     * @return Object[][] containing invalid status values
     * Invalid status values for negative testing
     */


    @DataProvider(name = "invalidStatusValues")
    public Object[][] getInvalidStatusValues() {
        return new Object[][]{
                {"invalid_status"},
                {"unknown"},
                {""},
                {"123"}
        };
    }

    /**
     * Provides pet data with invalid field types for negative testing
     *
     * @return Object[][] containing pets with invalid data
     * Invalid pet data for negative Create Pet tests
     * Covers missing required fields and empty strings
     */


    @DataProvider(name = "invalidPetData")
    public Object[][] getInvalidPetData() {
        return new Object[][]{
                // Pet with missing name (required field)
                {Pet.builder()
                        .photoUrls(Collections.singletonList("https://example.com/photo.jpg"))
                        .status(STATUS_AVAILABLE)
                        .build()},
                // Pet with missing photoUrls (required field)
                {Pet.builder()
                        .name("InvalidPet")
                        .status(STATUS_AVAILABLE)
                        .build()},
                // Pet with empty name
                {Pet.builder()
                        .name("")
                        .photoUrls(Collections.singletonList("https://example.com/photo.jpg"))
                        .build()}
        };
    }

    /**
     * Provides pet update data for testing status transitions
     *
     * @return Object[][] containing original and updated pet data
     * Pet update data for testing status transitions
     * Used in PUT operations to validate updates
     */


    @DataProvider(name = "petUpdateData")
    public Object[][] getPetUpdateData() {
        return new Object[][]{
                {10001L, "Buddy", STATUS_AVAILABLE, "Buddy Updated", STATUS_SOLD},
                {10002L, "Max", STATUS_PENDING, "Max Updated", STATUS_AVAILABLE},
                {10003L, "Whiskers", STATUS_SOLD, "Whiskers Updated", STATUS_PENDING}
        };
    }

    /**
     * Helper method to create a complete Pet object
     * Used by multiple data providers to reduce duplication
     *
     * @param id Pet ID
     * @param categoryName Category name
     * @param petName Pet name
     * @param status Pet status
     * @param tagName Tag name
     * @return Pet object with complete data
     */
    private Pet createPet(Long id, String categoryName, String petName, String status, String tagName) {
        Category category = Category.builder()
                .id(1L)
                .name(categoryName)
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .name(tagName)
                .build();

        return Pet.builder()
                .id(id)
                .category(category)
                .name(petName)
                .photoUrls(Collections.singletonList("https://example.com/" + petName.toLowerCase() + ".jpg"))
                .tags(Collections.singletonList(tag))
                .status(status)
                .build();
    }
}