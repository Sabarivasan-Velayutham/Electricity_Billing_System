package com.electricity.Controller;

import com.electricity.Exceptions.UserException;
import com.electricity.Model.Admin;
import com.electricity.Model.Bill;
import com.electricity.Model.User;
import com.electricity.service.AdminService;
import com.electricity.service.BeanFactory;
import com.electricity.service.BillingService;
import com.electricity.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Scanner;

public class AdminCLIManager {
    private AdminService adminService;
    private UserService userService;
    private BillingService billingService;
    private Scanner scanner;
    private Admin admin;
    boolean useDB;
    Connection conn;
    Statement statement;
    ResultSet rs;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCLIManager.class);

    public AdminCLIManager(boolean useDB) throws SQLException {
        // Initialize services using BeanFactory
        if (useDB) {
            this.adminService = (AdminService) BeanFactory.getBean("adminServiceDB");
            this.userService = (UserService) BeanFactory.getBean("userServiceDB");
            this.billingService = (BillingService) BeanFactory.getBean("billingServiceDB");
        } else {
            this.adminService = (AdminService) BeanFactory.getBean("adminServiceMM");
            this.userService = (UserService) BeanFactory.getBean("userServiceMM");
            this.billingService = (BillingService) BeanFactory.getBean("billingServiceMM");
        }
        this.scanner = new Scanner(System.in);
        this.useDB = useDB;
        conn = DataBaseConnectionManager.getConnection();
    }

    public void start() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                Welcome to the Admin Panel!                   ║");
        System.out.println("║                                                              ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        LOGGER.info("Admin panel started.");

        while (true) {
            displayAdminOptions();
            int choice = getUserChoice();
            handleAdminChoice(choice);
            if (choice == 2) {
                System.out.println("\n🔌 Exiting Admin Panel. 🔌");
                LOGGER.info("Admin panel exited.");
                break;
            }
        }
    }

    private void displayAdminOptions() {
        System.out.println("\n👨‍💼 Admin Options:");
        System.out.println("1. Login");
        System.out.println("2. Exit");
    }

    private int getUserChoice() {
        while (true) {
            try {
                System.out.print("\nEnter choice: ");
                return scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number. ❌");
                LOGGER.error("Input mismatch exception: {}", e.getMessage());
                scanner.nextLine(); // Consume the invalid input to avoid an infinite loop
            }
        }
    }

    private void handleAdminChoice(int choice) {
        try {
            scanner.nextLine();
            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    System.out.println("admin logout");
                    return;
                default:
                    System.out.println("\n❌ Invalid choice. Please try again. ❌\n");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter a valid number. ❌\n");
            LOGGER.error("Input mismatch exception: {}", e.getMessage());
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ An unexpected error occurred. Please try again. ❌\n");
            LOGGER.error("Error in handling admin choice: {}", e.getMessage());
        }
    }

    private void adminLogin() {
        String adminId = getValidInput("🔑 Enter Admin ID: ");
        String password = getValidInput("🔑 Enter Password: ");

        if (adminService.authenticateUser(adminId, password)) {
            System.out.println("\n Login successful! \n");
            admin = adminService.getUserById(adminId);
            adminMenu();
        } else {
            System.out.println("\n❌ Login failed. Admin not found or invalid credentials. ❌\n");
            LOGGER.warn("Admin login failed for admin ID: {}", adminId);
        }
    }

    private void adminMenu() {
        while (true) {
            displayAdminMenu();
            int choice = Integer.parseInt(getValidInput("\nEnter choice: "));
            handleAdminMenuChoice(choice);
            if (choice == 8) {
                return;
            }
        }
    }

    private void displayAdminMenu() {
        System.out.println("\n👨‍💼 Admin Menu:");
        System.out.println("1. Add User");
        System.out.println("2. View All Users");
        System.out.println("3. View User Bills");
        System.out.println("4. Add Bill for User");
        System.out.println("5. DeleteUser");
        System.out.println("6. UpdateUserDetails");
        System.out.println("7. SearchUserById");
        System.out.println("8. Logout");
    }

    private void handleAdminMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addUser();
                break;
            case 2:
                viewAllUsers();
                break;
            case 3:
                viewUserBills();
                break;
            case 4:
                addBillForUser();
                break;
            case 5:
                deleteUser();
                break;
            case 6:
                updateUserDetails();
                break;
            case 7:
                searchUserById();
                break;
            case 8:
                System.out.println("\nLogging out.\n");
                LOGGER.info("Admin logged out.");
                admin = null;
                return; // Return from this method to exit adminMenu loop
            default:
                System.out.println("\n❌ Invalid choice. Please try again. ❌\n");
                LOGGER.warn("Invalid choice selected in admin menu: {}", choice);
        }
    }

    private void addUser() {
        System.out.println("\n👤 Add User:");
        String name = getValidInput("Enter User Name: ");
        String address = getValidInput("Enter Address: ");
        String password = getValidInput("Enter Password: ");
        String fileName = getValidInput("Enter file name: ");
        if (useDB) {
            try {
                String query = String.format("INSERT INTO %s VALUES ('%s', '%s', '%s');", DataBaseConnectionManager.getUserTableName(), name, password, address);
                statement = conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("\n User added successfully in DB! \n");
                LOGGER.info("User added successfully with name: {}", name);
            } catch (SQLException e) {
                System.out.println("❌ Error adding user to the database. Please try again. ❌\n");
                LOGGER.error("Error adding user to the database: {}", e.getMessage());
            }
        }
        else{
            if (fileName == null) {
                userService.createUser(name, address, password);
            } else {
                userService.createUser(name, address, password, fileName);
                System.out.println("\n User added successfully! \n");
                LOGGER.info("User added successfully with name: {}", name);
            }
        }
    }

    private void viewAllUsers() {
        if (useDB) {
            try {
                System.out.println("\nUser details from DB:");

                String query = String.format("SELECT username, address FROM %s", DataBaseConnectionManager.getUserTableName());
                try (PreparedStatement preparedStatement = conn.prepareStatement(query);
                     ResultSet rs = preparedStatement.executeQuery()) {

                    while (rs.next()) {
                        System.out.print(rs.getString("username") + " ");
                        System.out.println(rs.getString("address") + " ");
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Error fetching users from the database. Please try again. ❌\n");
                LOGGER.error("Error fetching users from the database: {}", e.getMessage());
            }
        }
        else{
            System.out.println("\n👥 All Users:");
            List<User> users = userService.getAllUsers();
            if (!users.isEmpty()) {
                users.forEach(user -> System.out.println(user.getName() + " - " + user.getAddress()));
            } else {
                System.out.println("❌ No users found. ❌");
            }
        }
    }

    private void viewUserBills() {
        String userId = getValidInput("\nEnter User ID: ");

        if (useDB) {
            try {
                String query = String.format("SELECT * FROM %s WHERE userid = ?",
                        DataBaseConnectionManager.getBillTableName());

                try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                    preparedStatement.setString(1, userId);

                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        System.out.println("\nBills from DB:");
                        while (rs.next()) {
                            System.out.print(rs.getString("userid") + " ");
                            System.out.print(rs.getString("billid") + " ");
                            System.out.print(rs.getString("billamount") + " ");
                            System.out.println(rs.getString("paid"));
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Error fetching bills from the database. Please try again. ❌\n");
                LOGGER.error("Error fetching bills from the database: {}", e.getMessage());
            }
        }
        else{
            List<Bill> userBills = userService.getUserBills(userId);
            if (userBills != null && !userBills.isEmpty()) {
                System.out.println("\n💸 User Bills:");
                userBills.forEach(System.out::println);
            } else {
                System.out.println("\n❌ No bills found for the specified user ID. ❌\n");
            }
        }
    }

    private void addBillForUser() {
        System.out.println("\n💳 Add Bill for User:");
        String userId = getValidInput("Enter User ID: ");
        double billAmount = getBillAmount();

        if (billAmount > 0) {
            billingService.addBillForUser(userId, billAmount);
            System.out.println("\n Bill added successfully for the user! \n");
            LOGGER.info("Bill added successfully for user with ID: {}", userId);
        }
    }

    private double getBillAmount() {
        double billAmount = -1;
        do {
            String input = getValidInput("Enter Bill Amount: ");
            try {
                billAmount = Double.parseDouble(input);
                if (billAmount <= 0) {
                    System.out.println("❌ Bill amount must be greater than 0. ❌\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid bill amount. Please enter a valid number. ❌\n");
            }
        } while (billAmount <= 0);
        return billAmount;
    }


    private void deleteUser() {
        System.out.println("\n👤 Delete User:");
        String userId = getValidInput("Enter User ID: ");

        if (useDB) {
            try {
                String query = String.format("DELETE FROM %s WHERE username = '%s'", DataBaseConnectionManager.getUserTableName(), userId);
                statement = conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("\n🗑️ User deleted successfully in DB! 🗑️\n");
                LOGGER.info("User deleted successfully with ID: {}", userId);
            } catch (SQLException e) {
                System.out.println("❌ Error deleting user from the database. Please try again. ❌\n");
                LOGGER.error("Error deleting user from the database: {}", e.getMessage());
            }
        }
        else{
            if (userService.deleteUser(userId)) {
                System.out.println("\n🗑️ User deleted successfully! 🗑️\n");
                LOGGER.info("User deleted successfully with ID: {}", userId);
            } else {
                System.out.println("\n❌ Failed to delete user. User not found. ❌\n");
                LOGGER.warn("Failed to delete user with ID: {}. User not found.", userId);
            }
        }
    }

    private void updateUserDetails() {
        System.out.println("\n👤 Update User Details:");
        String userId = getValidInput("Enter User ID: ");
        User user = userService.getUserById(userId);
        System.out.println("Choose options to update User Details:");
        System.out.println("1. Update UserName: " + user.getName());
        System.out.println("2. Update UserAddress: " + user.getAddress());
        System.out.println("3. Update UserPassword: " + user.getPasswordHash());
        int choice = Integer.parseInt(getValidInput("\nEnter choice: "));

        if (useDB) {
            try {
                conn = DataBaseConnectionManager.getConnection();
                switch (choice) {
                    case 1:
                        String newName = getValidInput("Enter New Name: ");
                        String oldName = user.getName();
                        user.setName(newName);
                        String queryUpdateName = String.format("UPDATE %s SET username='%s' WHERE username='%s'",
                                DataBaseConnectionManager.getUserTableName(), newName, oldName);
                        statement = conn.createStatement();
                        statement.executeUpdate(queryUpdateName);
                        System.out.println("Data Updated");
                        break;
                    case 2:
                        String newAddress = getValidInput("Enter New Address: ");
                        String oldAddress = user.getAddress();
                        user.setAddress(newAddress);
                        String queryUpdateAddress = String.format("UPDATE %s SET address='%s' WHERE address='%s'",
                                DataBaseConnectionManager.getUserTableName(), newAddress, oldAddress);
                        statement = conn.createStatement();
                        statement.executeUpdate(queryUpdateAddress);
                        System.out.println("Data Updated");
                        break;
                    case 3:
                        String newPassword = getValidInput("Enter New Password: ");
                        String oldPasswordHash = user.getPasswordHash();
                        user.setPasswordHash(newPassword);
                        String queryUpdatePassword = String.format("UPDATE %s SET password='%s' WHERE password='%s'",
                                DataBaseConnectionManager.getUserTableName(), newPassword, oldPasswordHash);
                        statement = conn.createStatement();
                        statement.executeUpdate(queryUpdatePassword);
                        System.out.println("Data Updated");
                        break;
                    default:
                        System.out.println("\n❌ Invalid choice. No changes made. ❌\n");
                        return;
                }
            } catch (SQLException e) {
                System.out.println("❌ Error updating user details in the database. Please try again. ❌\n");
                LOGGER.error("Error updating user details in the database: {}", e.getMessage());
            }
        } else {
            switch (choice) {
                case 1:
                    String newName = getValidInput("Enter New Name: ");
                    user.setName(newName);
                    break;
                case 2:
                    String newAddress = getValidInput("Enter New Address: ");
                    user.setAddress(newAddress);
                    break;
                case 3:
                    String newPassword = getValidInput("Enter New Password: ");
                    user.setPasswordHash(newPassword);
                    break;
                default:
                    System.out.println("\n❌ Invalid choice. No changes made. ❌\n");
                    return;
            }
            try {
                userService.updateUser(user);
            } catch (UserException e) {
                LOGGER.error(String.valueOf(e));
            }
            System.out.println("\n User details updated successfully! \n");
            LOGGER.info("User details updated successfully for user ID: {}", userId);
        }
    }

    private void searchUserById() {
        String userId = getValidInput("\nEnter User ID: ");

        if(useDB){
            try {
                conn = DataBaseConnectionManager.getConnection();
                System.out.println("\nUser from Db:");

                String querySearchById = String.format("SELECT username, address FROM %s WHERE username=?", DataBaseConnectionManager.getUserTableName());

                try (PreparedStatement preparedStatement = conn.prepareStatement(querySearchById)) {
                    preparedStatement.setString(1, userId);

                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        while (rs.next()) {
                            System.out.print(rs.getString("username") + " ");
                            System.out.println(rs.getString("address"));
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Error searching user by ID in the database. Please try again. ❌\n");
                LOGGER.error("Error searching user by ID in the database: {}", e.getMessage());
            }
        }
        else{
            User user = userService.getUserById(userId);
            if (user != null) {
                System.out.println("\n🔍 User Details:");
                System.out.println("Name: " + user.getName());
                System.out.println("Address: " + user.getAddress());
            } else {
                System.out.println("\n❌ No user found with the specified ID. ❌\n");
            }
        }
    }

    private String getValidInput(String message) {
        String input;
        do {
            System.out.print(message);
            input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("0")) {
                System.out.println("❌ Input cannot be empty or zero. Please enter a valid input. ❌\n");
            }
        } while (input.isEmpty() || input.equals("0"));
        return input;
    }
}


