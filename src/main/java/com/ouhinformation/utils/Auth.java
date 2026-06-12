package com.ouhinformation.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for Authentication-related logic such as processing passwords.
 */
public class Auth {

    /**
     * Hashes a password using SHA-256 and encodes the result in Base64.
     * Useful for safely storing passwords in a database.
     *
     * @param password The plaintext password.
     * @return The hashed password string.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password. Algorithm not found.", e);
        }
    }

    /**
     * Verifies if a given plaintext password matches the stored hashed password.
     *
     * @param plainTextPassword The plaintext password input by user.
     * @param hashedPassword    The hashed password from the database.
     * @return true if they match linearly, false otherwise.
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        String newlyHashed = hashPassword(plainTextPassword);
        return newlyHashed.equals(hashedPassword);
    }
}
