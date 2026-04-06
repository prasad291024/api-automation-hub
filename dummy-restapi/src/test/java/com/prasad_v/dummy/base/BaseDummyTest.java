package com.prasad_v.dummy.base;

import com.prasad_v.config.EnvironmentManager;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class BaseDummyTest {

    @BeforeSuite(alwaysRun = true)
    public void initEnvironment() {
        EnvironmentManager.getInstance().initializeEnvironment();
    }

    @BeforeMethod(alwaysRun = true)
    public void throttle() throws InterruptedException {
        // Dummy REST API enforces ~1 req/sec rate limit — wait between each test
        Thread.sleep(2000);
    }

    /**
     * Executes a request and retries up to 3 times with 5s backoff if 429 is returned.
     * If all retries are exhausted, returns the last response (429) for test assertions.
     */
    protected Response executeWithRetry(Supplier<Response> request) {
        final int[] attempts = {0};
        final Response[] result = {null};
        try {
            Awaitility.await()
                    .atMost(30, TimeUnit.SECONDS)
                    .pollInterval(5, TimeUnit.SECONDS)
                    .until(() -> {
                        attempts[0]++;
                        result[0] = request.get();
                        return result[0].getStatusCode() != 429;
                    });
        } catch (ConditionTimeoutException e) {
            // All retries exhausted — return last response for assertion
        }
        return result[0];
    }
}
