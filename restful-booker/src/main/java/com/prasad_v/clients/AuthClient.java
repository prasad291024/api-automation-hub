package com.prasad_v.clients;

import com.prasad_v.services.AuthService;

/**
 * Client abstraction for authentication operations.
 * Wraps AuthService and manages token caching for the test session.
 */
public class AuthClient {

    private final AuthService authService;
    private String cachedToken;

    public AuthClient() {
        this.authService = new AuthService();
    }

    public AuthClient(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Returns an existing cached token if available, or fetches a new one.
     *
     * @return authentication token
     */
    public synchronized String getOrCreateToken() {
        if (cachedToken == null || cachedToken.isBlank()) {
            cachedToken = authService.getAuthToken();
        }
        return cachedToken;
    }

    /**
     * Forces the generation of a fresh token and updates the cache.
     *
     * @return freshly generated authentication token
     */
    public synchronized String getFreshToken() {
        cachedToken = authService.getAuthToken();
        return cachedToken;
    }

    /**
     * Authenticates with explicit credentials.
     *
     * @param username username
     * @param password password
     * @return authentication token
     */
    public String authenticate(String username, String password) {
        return authService.getAuthToken(username, password);
    }

    /**
     * Invalidate the current cached token.
     */
    public synchronized void invalidateToken() {
        this.cachedToken = null;
    }
}
