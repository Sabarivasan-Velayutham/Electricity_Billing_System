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
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class AdminCLIManager {
    private AdminService adminService;
    private UserService userService;
    private BillingService billingService;
    private Scanner scanner;
    private Admin admin;
    String dbname="electricitybillsystem";
    String user="postgres";
    String pass="sabari";
    String table_name="userinfo";
    Connection conn=null;
    Statement statement;
    ResultSet rs=null;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCLIManager.class);

    public AdminCLIManager() {
        // Initialize services using BeanFactory
        this.adminService = (AdminService) BeanFactory.getBean("adminService");
        this.userService = (UserService) BeanFactory.getBean("userService");
        this.billingService = (BillingService) BeanFactory.getBean("billingService");
        this.scanner = new Scanner(System.in);
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
           scanner.nextLine(); // Consume newline character
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
        String adminId=getValidInput("🔑 Enter Admin ID: ");
        String password=getValidInput("🔑 Enter Password: ");

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
        if (fileName == null) {
            userService.createUser(name, address, password);
        } else {
            userService.createUser(name, address, password, fileName);
            System.out.println("\n User added successfully! \n");
            LOGGER.info("User added successfully with name: {}", name);
        }
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        Statement statement;
        try {
            String query=String.format("insert into %s values('%s','%s','%s');",table_name,name,password,address);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row Inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void viewAllUsers() {
        System.out.println("\n👥 All Users:");
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            users.forEach(user -> System.out.println(user.getName() + " - " + user.getAddress()));
        } else {
            System.out.println("❌ No users found. ❌");
        }
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            String query=String.format("select * from %s",table_name);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.print(rs.getString("username")+" ");
                System.out.println(rs.getString("address")+" ");
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private void viewUserBills() {
        String userId = getValidInput("\nEnter User ID: ");
        List<Bill> userBills = userService.getUserBills(userId);
        if (userBills != null) {
            System.out.println("\n💸 User Bills:");
            userBills.forEach(System.out::println);
        } else {
            System.out.println("\n❌ No bills found for the specified user ID. ❌\n");
        }
        String table_name="billdata";
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            String query=String.format("select * from billdata where userid= '%s'",userId);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()){
                System.out.print(rs.getString("userid")+" ");
                System.out.print(rs.getString("billid")+" ");
                System.out.print(rs.getString("billamount")+" ");
                System.out.println(rs.getString("paid"));
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void addBillForUser() {
        System.out.println("\n💳 Add Bill for User:" );
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
        System.out.println("\n👤 Delete User:" );
        String userId = getValidInput("Enter User ID: ");
        if (userService.deleteUser(userId)) {
            System.out.println("\n🗑️ User deleted successfully! 🗑️\n");
            LOGGER.info("User deleted successfully with ID: {}", userId);
        } else {
            System.out.println("\n❌ Failed to delete user. User not found. ❌\n");
            LOGGER.warn("Failed to delete user with ID: {}. User not found.", userId);
        }
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try{
            String query=String.format("delete from %s where username='%s'",table_name,userId);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
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
            try{
                Class.forName("org.postgresql.Driver");
                conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres",pass);
                if(conn!=null){
                    System.out.println("Connection Established");
                }
                else{
                    System.out.println("Connection Failed");
                }    
            }
            catch (Exception e){
                System.out.println(e);
            }
            switch (choice) {
                case 1:
                    String newName = getValidInput("Enter New Name: ");
                    user.setName(newName);
                    try {
                        String query=String.format("update %s set username='%s' where username='%s'",table_name,newName,user.getName());
                        statement=conn.createStatement();
                        statement.executeUpdate(query);
                        System.out.println("Data Updated");
                    }catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 2:
                    String newAddress = getValidInput("Enter New Address: ");
                    user.setAddress(newAddress);
                    try {
                        String query=String.format("update %s set address='%s' where address='%s'",table_name,newAddress,user.getAddress());
                        statement=conn.createStatement();
                        statement.executeUpdate(query);
                        System.out.println("Data Updated");
                    }catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                case 3:
                    String newPassword = getValidInput("Enter New Password: ");
                    user.setPasswordHash(newPassword);
                    try {
                        String query=String.format("update %s set password='%s' where password='%s'",table_name,newPassword,user.getPasswordHash());
                        statement=conn.createStatement();
                        statement.executeUpdate(query);
                        System.out.println("Data Updated");
                    }catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                default:
                    System.out.println("\n❌ Invalid choice. No changes made. ❌\n");
                    return;
            }
    }

    private void searchUserById() {
        String userId = getValidInput("\nEnter User ID: ");
        User user = userService.getUserById(userId);
        if (user != null) {
            System.out.println("\n🔍 User Details:");
            System.out.println("Name: " + user.getName());
            System.out.println("Address: " + user.getAddress());
        } else {
            System.out.println("\n❌ No user found with the specified ID. ❌\n");
        }
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres",pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            String query=String.format("select * from %s where username='%s'",table_name,userId);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()){
                System.out.print(rs.getString("username")+" ");
                System.out.println(rs.getString("address"));

            }
        }catch (Exception e){
            System.out.println(e);
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


