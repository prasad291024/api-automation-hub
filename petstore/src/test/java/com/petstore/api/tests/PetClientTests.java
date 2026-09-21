package com.petstore.api.tests;

import com.petstore.api.asserts.PetAssertions;
import com.petstore.api.clients.PetClient;
import com.petstore.api.config.Config;
import com.petstore.api.models.Category;
import com.petstore.api.models.Pet;
import com.petstore.api.models.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

/**
 * Validates Petstore API using high-level PetClient domain abstraction.
 */
@Epic("Petstore API Automation")
@Feature("Pet Management - Client Layer")
public class PetClientTests {

    private PetClient petClient;

    @BeforeClass
    public void setUp() {
        petClient = new PetClient();
    }

    @Test(priority = 1)
    @Story("Create and Read Pet")
    @Description("Verify creating and retrieving a pet using PetClient")
    public void testCreateAndGetPetViaClient() {
        long petId = System.currentTimeMillis() % 100000000L;
        Pet petPayload = Pet.builder()
                .id(petId)
                .name("ClientTestPet")
                .category(Category.builder().id(1L).name("Dogs").build())
                .photoUrls(Collections.singletonList("https://example.com/pet.jpg"))
                .tags(Collections.singletonList(Tag.builder().id(1L).name("playful").build()))
                .status(Config.STATUS_AVAILABLE)
                .build();

        // Create Pet
        Pet created = petClient.createPet(petPayload);
        PetAssertions.verifyPetDetails(created, petPayload);

        // Retrieve Pet
        Pet retrieved = petClient.getPet(petId);
        PetAssertions.verifyPetDetails(retrieved, petPayload);
    }

    @Test(priority = 2)
    @Story("Update Pet")
    @Description("Verify updating pet status via PetClient")
    public void testUpdatePetViaClient() {
        long petId = (System.currentTimeMillis() + 100) % 100000000L;
        Pet petPayload = Pet.builder()
                .id(petId)
                .name("UpdatablePet")
                .status(Config.STATUS_AVAILABLE)
                .build();

        petClient.createPet(petPayload);

        // Update status to SOLD
        petPayload.setStatus(Config.STATUS_SOLD);
        Pet updated = petClient.updatePet(petPayload);
        PetAssertions.verifyPetStatus(updated, Config.STATUS_SOLD);
    }

    @Test(priority = 3)
    @Story("Delete Pet")
    @Description("Verify deleting a pet via PetClient")
    public void testDeletePetViaClient() {
        long petId = (System.currentTimeMillis() + 200) % 100000000L;
        Pet petPayload = Pet.builder()
                .id(petId)
                .name("DeletablePet")
                .status(Config.STATUS_AVAILABLE)
                .build();

        petClient.createPet(petPayload);

        boolean deleted = petClient.deletePet(petId);
        Assert.assertTrue(deleted, "Pet deletion should succeed");
    }

    @Test(priority = 4)
    @Story("Find by Status")
    @Description("Verify finding available pets via PetClient")
    public void testFindPetsByStatusViaClient() {
        List<Pet> pets = petClient.findByStatus(Config.STATUS_AVAILABLE);
        Assert.assertNotNull(pets, "Pets list should not be null");
        Assert.assertFalse(pets.isEmpty(), "Pets list should not be empty");
    }
}
