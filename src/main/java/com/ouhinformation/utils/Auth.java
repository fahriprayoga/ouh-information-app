package com.ouhinformation.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/**
 * Utility class for Authentication-related logic such as processing passwords and logging in.
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

    /**
     * Authenticates a user based on username and password against the MongoDB database.
     *
     * @param username The username input.
     * @param password The plaintext password input.
     * @return true if authentication is successful, false otherwise.
     */
    public static boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;

        MongoDatabase database = MongoConfig.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("users");

        Document user = usersCollection.find(Filters.eq("username", username)).first();
        if (user != null) {
            String storedPassword = user.getString("password");
            return verifyPassword(password, storedPassword);
        }
        return false;
    }
}
