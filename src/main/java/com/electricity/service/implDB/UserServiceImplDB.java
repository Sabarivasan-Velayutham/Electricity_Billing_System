
package com.electricity.service.implDB;

import com.electricity.Controller.DataBaseConnectionManager;
import com.electricity.Exceptions.UserException;
import com.electricity.Model.Admin;
import com.electricity.Model.Bill;
import com.electricity.Model.User;
import com.electricity.service.AbstractUserService;
import com.electricity.service.AuthenticationHandler;
import com.electricity.service.BeanFactory;
import com.electricity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImplDB extends AbstractUserService implements UserService {
    private final AuthenticationHandler authenticationHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImplDB.class);

    public UserServiceImplDB() {
        this.authenticationHandler = (AuthenticationHandler) BeanFactory.getBean("adminService.authenticationHandlerImpl");
    }

    @Override
    public void createUser(String name, String address, String password) {
        String userId = name;
        insertUserDataIntoDB(userId, address, password);
        LOGGER.info("User created successfully with ID: {}", userId);
    }

    @Override
    public void createUser(String name, String address, String password, String fileLocation) {
        String userId = name;
        insertUserDataIntoDB(userId,  address, password);
        LOGGER.info("User created successfully with ID: {}", userId);
        appendUserDataToCustomLocation(userId, name, address, fileLocation);
    }

    @Override
    protected void appendUserDataToCustomLocation(User user, String fileLocation) {

    }

    @Override
    protected void appendUserDataToCustomLocation(String userId, String name, String address, String fileLocation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, true))) {
            writer.write("User ID: " + userId + "\n");
            writer.write("Name: " + name + "\n");
            writer.write("Address: " + address + "\n");
            writer.write("---------------------------------\n");

            LOGGER.info("User data appended to file: {}", fileLocation);
        } catch (IOException e) {
            LOGGER.error("Error appending user data to file: {}", e.getMessage());
        }
    }

    private void insertUserDataIntoDB(String userId,  String address, String password) {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getUserTableName();
            String query = "INSERT INTO " + tableName + " (username, password, address) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, address);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error inserting user data into the database: {}", e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getUserTableName();
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String userId = resultSet.getString("username");
                    String name = resultSet.getString("name");
                    String address = resultSet.getString("address");
                    userList.add(new User(userId, name, address, ""));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all users from the database: {}", e.getMessage());
        }
        return userList;
    }

    @Override
    public List<Bill> getUserBills(String userId) {
        return null; // Implement this method if needed
    }

    @Override
    public User getUserById(String userId) {
        String query = "SELECT * FROM userinfo WHERE username = ?";
        User user = null;

        try (Connection connection = DataBaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUserId = resultSet.getString("username");
                    System.out.println("Retrieved admin: " + retrievedUserId);

                    // Create an Admin object using retrieved data
                    return new User(
                            retrievedUserId,
                            resultSet.getString("username"),
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
        return user;
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        String selectUserSQL = "SELECT * FROM userinfo WHERE username = ? AND password = ?";
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
            e.printStackTrace();
            System.out.println("Error retrieving user from the database: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void updateUserDetails(String userId, String newName, String newAddress, String newPassword) {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getUserTableName();
            StringBuilder queryBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            List<String> updates = new ArrayList<>();

            if (newName != null && !newName.isEmpty()) {
                updates.add("name = '" + newName + "'");
            }

            if (newAddress != null && !newAddress.isEmpty()) {
                updates.add("address = '" + newAddress + "'");
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                updates.add("password = '" + newPassword + "'");
            }

            queryBuilder.append(String.join(", ", updates));
            queryBuilder.append(" WHERE username = ?");

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
                preparedStatement.setString(1, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating user details in the database: {}", e.getMessage());
        }
    }

    @Override
    public boolean deleteUser(String userId) {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getUserTableName();
            String query = "DELETE FROM " + tableName + " WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                int rowsDeleted = preparedStatement.executeUpdate();
                LOGGER.info("User deleted successfully with ID: {}", userId);
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting user from the database: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void updateUser(User user) throws UserException {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getUserTableName();
            String query = "UPDATE " + tableName + " SET name = ?, address = ?, password = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getAddress());
                preparedStatement.setString(3, user.getPasswordHash());
                preparedStatement.setString(4, user.getUserId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new UserException("User not found with ID: " + user.getUserId());
                }
                LOGGER.info("User details updated successfully for user ID: {}", user.getUserId());
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating user details in the database: {}", e.getMessage());
            throw new UserException("Error updating user details");
        }
    }
}
