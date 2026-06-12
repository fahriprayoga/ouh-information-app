package com.ouhinformation.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing the current user session across the application.
 * This is implemented as a Singleton so the session data is shared globally.
 */
public class Session {

    private static Session instance;

    // A flexible map to store any session attributes
    private final Map<String, Object> sessionData;

    private Session() {
        sessionData = new HashMap<>();
    }

    /**
     * Retrieves the Singleton instance of the Session.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Sets an attribute in the session.
     *
     * @param key   The key for the attribute.
     * @param value The value of the attribute.
     */
    public void setAttribute(String key, Object value) {
        sessionData.put(key, value);
    }

    /**
     * Gets an attribute from the session.
     *
     * @param key The key of the attribute.
     * @return The value associated with the key, or null if not found.
     */
    public Object getAttribute(String key) {
        return sessionData.get(key);
    }

    /**
     * Removes an attribute from the session.
     *
     * @param key The key of the attribute to remove.
     */
    public void removeAttribute(String key) {
        sessionData.remove(key);
    }

    /**
     * Clears all session data (useful for logout).
     */
    public void clearSession() {
        sessionData.clear();
    }

    // ========== Convenience Methods for Common Data ==========

    public void setUserId(int id) {
        setAttribute("userId", id);
    }

    public Integer getUserId() {
        Object id = getAttribute("userId");
        return id instanceof Integer ? (Integer) id : null;
    }

    public void setUsername(String username) {
        setAttribute("username", username);
    }

    public String getUsername() {
        Object username = getAttribute("username");
        return username instanceof String ? (String) username : null;
    }

    public void setRole(String role) {
        setAttribute("role", role);
    }

    public String getRole() {
        Object role = getAttribute("role");
        return role instanceof String ? (String) role : null;
    }
    
    /**
     * Checks if a user is currently logged in (based on userId existence).
     */
    public boolean isLoggedIn() {
        return getUserId() != null;
    }
}
