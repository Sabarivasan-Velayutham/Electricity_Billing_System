package com.electricity.service;

import com.electricity.Model.Admin;
import com.electricity.Model.Bill;
import com.electricity.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

public class AdminServiceImpl extends  AbstractAdminService implements AdminService, Serializable {

    private Map<String, Admin> admins;
    private Map<String, User> users;
    private Map<String, List<Bill>> userBills;

    private final AuthenticationHandlerImpl authenticationHandlerImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    public AdminServiceImpl() {
        this.admins = new HashMap<>();
        this.users = new HashMap<>();
        this.userBills = new HashMap<>();
        this.authenticationHandlerImpl = (AuthenticationHandlerImpl) BeanFactory.getBean("adminService.authenticationHandlerImpl");
        // Manually create and populate five users
        createAndPopulateUsers();
    }

    // Manually create and populate five users
    private void createAndPopulateUsers() {
        // Create five users with unique IDs and passwords
        for (int i = 1; i <= 5; i++) {
            String userId = "user" + i;
            String name = "User " + i;
            String address = "Address " + i;
            String password = "password" + i; // Assuming passwords are "password1", "password2", ..., "password5"
            Admin user = new Admin(userId, address, password);
            admins.put(userId, user);
        }
        serializeToFile();
    }

    @Override
    public void addUser(String name, String address, String password) {
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, name, address, password);
        users.put(userId, user);
        userBills.put(userId, new ArrayList<>());
        LOGGER.info("User created successfully with ID: {}", userId);
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        AdminServiceImpl deserializedService = AdminServiceImpl.deserializeFromFile();
        Admin admin = null;
        if (deserializedService != null) {
            admin = deserializedService.getUserById(userId);
            if (admin != null) {
                System.out.println("Retrieved user: " + admin.getAdminId());
            }
        }
        if (admin != null) {
            return authenticationHandlerImpl.authenticateAdmin(admin, userId, password);
        }
        return false;
    }

    @Override
    public Admin getUserById(String userId) {
        return admins.get(userId);
    }

    @Override
    public void addBillForUser(String userId, double amount) {
        List<Bill> bills = userBills.getOrDefault(userId, new ArrayList<>());
        String billId = UUID.randomUUID().toString();
        Bill bill = new Bill(billId, userId, amount);
        bills.add(bill);
        userBills.put(userId, bills);
        LOGGER.info("Bill added successfully for user ID: {}", userId);
    }

    // Serialization methods
    public void serializeToFile() {
        String filename = "object.txt";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this); // Serialize the current object
            LOGGER.info("Object serialized to file: {}", filename);
        } catch (IOException e) {
            LOGGER.error("Error serializing object to file: {}", e.getMessage());
        }
    }
    public static AdminServiceImpl deserializeFromFile() {
        String filename = "object.txt";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            AdminServiceImpl adminService = (AdminServiceImpl) ois.readObject(); // Deserialize the object
            LOGGER.info("Object deserialized from file: {}", filename);
            return adminService;
        } catch (IOException e) {
            LOGGER.error("IO error while deserializing object from file '{}': {}", filename, e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class not found error while deserializing object from file '{}': {}", filename, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error during deserialization from file '{}': {}", filename, e.getMessage());
        }
        return null;
    }
}
