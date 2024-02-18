package com.electricity.Model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Admin model class
public class Admin implements Serializable{
    private String adminId;
    private String username;


    private String passwordHash;
    public Admin(String adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = hashPassword(password);
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public boolean checkPassword(String password) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(passwordHash);
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Override toString for logging or debugging purposes
    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
