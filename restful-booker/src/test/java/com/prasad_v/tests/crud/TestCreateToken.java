package com.prasad_v.tests.crud;

import com.prasad_v.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

/**
 * Validates authentication API by requesting an auth token via AuthService.
 */
public class TestCreateToken extends BaseTest {

    /**
     * Test Case ID: TC#2
     * Validates successful authentication token generation using AuthService abstraction.
     */
    @Test(groups = {"reg", "smoke"}, priority = 1)
    @TmsLink("https://bugz.atlassian.net/browse/TS-1")
    @Owner("Promode")
    @Description("TC#2 - Create Token and Verify using AuthService")
    public void testTokenPOST() {
        // Send request via AuthService abstraction
        response = authService.createTokenResponse(payloadManager.setAuthPayload());

        // Validate status code
        assertActions.verifyStatusCode(response, 200);

        // Extract and verify token
        String token = payloadManager.getTokenFromJSON(response.asString());
        assertActions.verifyStringKeyNotNull(token);
    }
}
