package com.electricity.service;

import com.electricity.Exceptions.UserException;
import com.electricity.Model.Bill;
import com.electricity.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl extends AbstractUserService implements UserService {
    private final Map<String, User> users;
    private final BillingService billingService;
    private final AuthenticationHandlerImpl authenticationHandlerImpl;

    private Map<String, List<Bill>> userBills;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    public UserServiceImpl() {
        this.users = new HashMap<>();
        this.userBills = new HashMap<>();
        this.billingService = (BillingService) BeanFactory.getBean("userService.billingService");
        this.authenticationHandlerImpl = (AuthenticationHandlerImpl) BeanFactory.getBean("adminService.authenticationHandlerImpl");
    }

    @Override
    public void createUser(String name, String address, String password) {
        String userId = name;
        User user = new User(userId, name, address, password);
        users.put(userId, user);
        LOGGER.info("User created successfully with ID: {}", userId);
        appendUserDataToFile(user);
    }

    @Override
    public void createUser(String name, String address, String password,String fileLocation) {
        String userId = name;
        User user = new User(userId, name, address, password);
        users.put(userId, user);
        LOGGER.info("User created successfully with ID: {}", userId);
        appendUserDataToCustomLocation(user,fileLocation);
    }


    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Bill> getUserBills(String userId) {
        return userBills.get(userId);
    }

    @Override
    public User getUserById(String userId) {
        return users.get(userId);
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        User user = users.get(userId);
        if (user != null) {
            return authenticationHandlerImpl.authenticateUser(user, userId, password);
        }
        return false;
    }

    @Override
    public void updateUserDetails(String userId, String newName, String newAddress, String newPassword) {
        User user = users.get(userId);
        if (user != null) {
            if (newName != null && !newName.isEmpty()) {
                user.setName(newName);
            }
            if (newAddress != null && !newAddress.isEmpty()) {
                user.setAddress(newAddress);
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPasswordHash(newPassword);
            }
            LOGGER.info("User details updated successfully for user ID: {}", userId);
        } else {
            LOGGER.warn("User not found with ID: {}", userId);
        }
    }



    @Override
    public boolean deleteUser(String userId) {
        User user = users.get(userId);
        if (user != null) {
            users.remove(userId);
            userBills.remove(userId); // Remove associated bills
            LOGGER.info("User deleted successfully with ID: {}", userId);
            return true;
        } else {
            LOGGER.warn("User not found with ID: {}. Deletion failed.", userId);
            return false;
        }
    }
    public void updateUser(User user) throws UserException {
        User user1 = users.get(user.getUserId());
        if (user1 != null) {
            user1.setName(user.getName());
            user1.setAddress(user.getAddress());
            user1.setPasswordHash(user.getPasswordHash());
            LOGGER.info("User details updated successfully for user ID: {}", user.getUserId());
        } else {
            LOGGER.warn("User not found with ID: {}", user.getUserId());
            throw new UserException("User not found with ID: " + user.getUserId());
        }
    }

    @Override
    protected void appendUserDataToCustomLocation(User user,String fileLocation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, true))) {
            writer.write("User ID: " + user.getUserId() + "\n");
            writer.write("Name: " + user.getName() + "\n");
            writer.write("Address: " + user.getAddress() + "\n");
            writer.write("---------------------------------\n");

            LOGGER.info("User data appended to file: {}",fileLocation);
        } catch (IOException e) {
            LOGGER.error("Error appending user data to file: {}", e.getMessage());
        }

    }

}



