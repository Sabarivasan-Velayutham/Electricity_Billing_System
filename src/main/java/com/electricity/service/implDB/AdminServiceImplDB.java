
package com.electricity.service.implDB;

import com.electricity.Controller.DataBaseConnectionManager;
import com.electricity.Model.Admin;
import com.electricity.Model.Bill;
import com.electricity.Model.User;
import com.electricity.service.AbstractAdminService;
import com.electricity.service.AdminService;
import com.electricity.service.AuthenticationHandlerImpl;
import com.electricity.service.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.UUID;

public class AdminServiceImplDB extends AbstractAdminService implements AdminService, Serializable {
    private final AuthenticationHandlerImpl authenticationHandlerImpl;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImplDB.class);
    public AdminServiceImplDB() {
        this.authenticationHandlerImpl = (AuthenticationHandlerImpl) BeanFactory.getBean("adminService.authenticationHandlerImpl");
        // Manually create and populate five users
        createAndPopulateUsers();
    }
    private void createAndPopulateUsers() {
        // Create five users with unique IDs and passwords
        for (int i = 1; i <= 5; i++) {
            String username = "user" + i;
            String password = "password" + i;
            String address = "Address " + i;

            // Insert user data into the database
            String insertUserSQL = "INSERT INTO " + DataBaseConnectionManager
                    .getUserTableName() + " (username, password, address) VALUES (?, ?, ?)";

            try (Connection connection = DataBaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, address);

                preparedStatement.executeUpdate();
                LOGGER.info("User created successfully with username: {}", username);

            } catch (SQLException e) {
                LOGGER.error("Error adding user to the database: {}", e.getMessage());
            }
        }
    }

    @Override
    public void addUser(String name, String address, String password) {
        // Insert user data into the database
        String insertUserSQL = "INSERT INTO " + DataBaseConnectionManager
                .getUserTableName() + " (username, password, address) VALUES (?, ?, ?)";

        try (Connection connection = DataBaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, address);

            preparedStatement.executeUpdate();
            LOGGER.info("User created successfully with username: {}", name);

        } catch (SQLException e) {
            LOGGER.error("Error adding user to the database: {}", e.getMessage());
        }
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        String selectUserSQL = "SELECT * FROM admininfo WHERE username = ? AND password = ?";
        try (Connection connection = DataBaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, password);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    System.out.println("Retrieved user: " + username);
                    return true;
                } else {
                    System.out.println("User with ID " + userId + " not found or invalid credentials.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user from the database: " + e.getMessage());
            return false;
        }
    }



    @Override
    public Admin getUserById(String userId) {
        String query = "SELECT * FROM userinfo WHERE username = ?";
        Admin admin = null;

        try (Connection connection = DataBaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUserId = resultSet.getString("username");
                    System.out.println("Retrieved admin: " + retrievedUserId);

                    // Create an Admin object using retrieved data
                    return new Admin(
                            retrievedUserId,
                            resultSet.getString("password"),
                            resultSet.getString("address")
                    );
                } else {
                    System.out.println("Admin with ID " + userId + " not found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving admin from the database: " + e.getMessage());
        }
        return admin;
    }


    @Override
    public void addBillForUser(String userId, double amount) {
        String insertBillSQL = "INSERT INTO billdata (userid, billid, billamount, paid) VALUES (?, ?, ?, false)";
        String billId = UUID.randomUUID().toString();

        try (Connection connection = DataBaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertBillSQL)) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, billId);
            preparedStatement.setDouble(3, amount);

            preparedStatement.executeUpdate();
            System.out.println("Bill added successfully for user ID: " + userId);

        } catch (SQLException e) {
            System.out.println("Error adding bill to the database: " + e.getMessage());
        }
    }
}
