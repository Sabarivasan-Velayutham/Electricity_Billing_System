package org.example;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, String> userCredentials;

    public UserManager() {
        userCredentials = new HashMap<>();
    }

    public void createUser(String username, String password) {
        userCredentials.put(username, password);
        System.out.println("User created. Username: " + username);
    }

    public boolean authenticateUser(String username, String password) {
        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            System.out.println("User authenticated successfully. Username: " + username);
            return true;
        } else {
            System.out.println("User authentication failed for username: " + username);
            return false;
        }
    }
}
