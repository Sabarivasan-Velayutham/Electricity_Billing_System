package com.electricity.service;

import com.electricity.Exceptions.UserException;
import com.electricity.Model.Bill;
import com.electricity.Model.User;

import java.util.List;


public interface UserService {
    void createUser(String name, String address, String password);

    void createUser(String name, String address, String password,String fileName);

    List<User> getAllUsers();
    User getUserById(String userId);

    List<Bill> getUserBills(String userId);
    boolean authenticateUser(String userId, String password);

    void updateUserDetails(String userId, String newName, String newAddress, String newPassword);

    void updateUser(User user) throws UserException;

    boolean deleteUser(String userId);
}