package com.prasad_v.dummy.base;

import com.prasad_v.config.EnvironmentManager;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.util.function.Supplier;

public class BaseDummyTest {

    // Dummy REST API is very aggressive with rate limiting in CI —
    // use longer delays and exponential backoff
    private static final int  MAX_RETRIES       = 8;
    private static final long INITIAL_BACKOFF_MS = 5000;  // 5s base
    private static final long MAX_BACKOFF_MS     = 60000; // 60s cap

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }

    @BeforeMethod(alwaysRun = true)
    public void throttle() throws InterruptedException {
        // Wait 5s before every test to respect the API rate limit in CI
        Thread.sleep(5000);
    }

    /**
     * Retries the request up to MAX_RETRIES times using exponential backoff
     * whenever a 429 Too Many Requests response is received.
     */
    protected Response executeWithRetry(Supplier<Response> request) {
        Response response = null;
        long backoff = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            response = request.get();
            if (response.getStatusCode() != 429) {
                return response;
            }
            try {
                System.out.printf("[DummyAPI] 429 received — attempt %d/%d, waiting %ds%n",
                        attempt, MAX_RETRIES, backoff / 1000);
                Thread.sleep(backoff);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            backoff = Math.min(backoff * 2, MAX_BACKOFF_MS); // exponential backoff with cap
        }
        return response; // return last response for assertion
    }
}
