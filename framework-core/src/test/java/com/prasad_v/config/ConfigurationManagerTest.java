package com.prasad_v.config;

import com.prasad_v.exceptions.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConfigurationManagerTest {

    private ConfigurationManager config;

    @BeforeMethod
    public void setUp() {
        config = ConfigurationManager.getInstance();
        config.clearProperties();
    }

    @Test
    public void testSetAndGetProperty() {
        config.setProperty("test.key", "test.value");
        Assert.assertEquals(config.getProperty("test.key"), "test.value");
    }

    @Test
    public void testGetPropertyWithDefault() {
        String value = config.getProperty("non.existent.key", "default_val");
        Assert.assertEquals(value, "default_val");
    }

    @Test
    public void testValidateRequiredPropertiesSuccess() {
        config.setProperty("app.url", "https://api.example.com");
        config.setProperty("app.timeout", "5000");
        // Should not throw any exception
        config.validateRequiredProperties("app.url", "app.timeout");
    }

    @Test(expectedExceptions = ConfigurationException.class)
    public void testValidateRequiredPropertiesMissingThrowsException() {
        config.setProperty("app.url", "https://api.example.com");
        config.validateRequiredProperties("app.url", "app.secret.key");
    }

    @Test(expectedExceptions = ConfigurationException.class)
    public void testValidateRequiredPropertiesBlankThrowsException() {
        config.setProperty("app.url", "   ");
        config.validateRequiredProperties("app.url");
    }
}
