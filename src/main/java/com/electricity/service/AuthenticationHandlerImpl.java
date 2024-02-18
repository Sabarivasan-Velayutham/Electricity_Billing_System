package com.electricity.service;

import com.electricity.Model.Admin;
import com.electricity.Model.User;
import com.electricity.Exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationHandlerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHandlerImpl.class);

    // Static initializer block to initialize MessageDigest once
    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            String errorMessage = "Failed to initialize MessageDigest: " + e.getMessage();
            LOGGER.error(errorMessage, e);
            throw new AuthenticationException(errorMessage, e);
        }
    }

    // Synchronized block to ensure thread safety during hashing
    public static synchronized String hashString(String input) {
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static boolean authenticateUser(User user, String encryptedUserId, String password) {
        String hashedPassword = hashString(password);
        boolean authenticated = encryptedUserId.equals(user.getUserId()) && hashedPassword.equals(user.getPasswordHash());
        if (!authenticated) {
            LOGGER.warn("User authentication failed for user ID: {}", encryptedUserId);
        }
        return authenticated;
    }

    public static boolean authenticateAdmin(Admin user, String encryptedUserId, String password) {
        String hashedPassword = hashString(password);
        boolean authenticated = encryptedUserId.equals(user.getAdminId()) && hashedPassword.equals(user.getPasswordHash());
        if (!authenticated) {
            LOGGER.warn("Admin authentication failed for admin ID: {}", encryptedUserId);
        }
        return authenticated;
    }
}
