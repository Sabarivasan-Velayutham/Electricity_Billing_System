package com.electricity.service;

import com.electricity.Model.User;


public interface AuthenticationHandler {

    String hashString(String name, String address, String password);

    boolean authenticateUser(User user, String encryptedUserId, String password);

}
