package com.electricity.Controller;

import com.electricity.Model.User;
import com.electricity.service.BeanFactory;
import com.electricity.service.BillingService;
import com.electricity.service.PaymentMethod;
import com.electricity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class UserCLIManager {
    private UserService userService;
    private BillingService billingService;
    private Scanner scanner;
    private User user;
    String dbname="electricitybillsystem";
    String userdb="postgres";
    String pass="sabari";
    String table_name="userinfo";
    Connection conn=null;
    Statement statement;
    ResultSet rs=null;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCLIManager.class);

    public UserCLIManager() {
        // Initialize services using BeanFactory
        this.userService = (UserService) BeanFactory.getBean("userService");
        this.billingService = (BillingService) BeanFactory.getBean("billingService");
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                Welcome to the User Panel!                    ║");
        System.out.println("║                                                              ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        System.out.println("Welcome to the User Panel!");
        LOGGER.info("User panel started.");

        while (true) {
            displayUserOptions();
            int choice = getUserChoice();
            handleUserChoice(choice);
            if (user != null && choice == 5) {
                System.out.println("🔌 Logging out. 🔌");
                LOGGER.info("User logged out.");
                user = null; // Clear user session
                return;
            } else if (choice == 3) {
                LOGGER.info("User exited");
                return;
            }
        }
    }

    private void displayUserOptions() {
        System.out.println("\n👨‍💼 User option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
    }

    private int getUserChoice() {
        while (true) {
            try {
                System.out.print("\nEnter choice: ");
                return scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number. ❌");
                LOGGER.error("Input mismatch exception: {}", e.getMessage());
                scanner.nextLine();
            }
        }
    }

    public void handleUserChoice(int choice) {
        try {
            scanner.nextLine(); // Consume newline character
            switch (choice) {
                case 1:
                    userLogin();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    LOGGER.info("User exited.");
                    return;
                default:
                    System.out.println("\n❌ Invalid choice. Please try again. ❌\n");
                    LOGGER.warn("Invalid choice selected: {}", choice);

            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter a valid number. ❌\n");
            LOGGER.error("Input mismatch exception: {}", e.getMessage());
            scanner.nextLine();
        } catch (Exception e) {
            // Handle other exceptions
            System.out.println("❌ An unexpected error occurred. Please try again. ❌\n");
            LOGGER.error("Error in handling admin choice: {}", e.getMessage());
        }
    }

    private void userLogin() {
        String userId=getValidInput("🔑 Enter User ID: ");
        String password=getValidInput("🔑 Enter Password: ");
        if (userService.authenticateUser(userId, password)) {
            System.out.println("\n Login successful! \n");
            LOGGER.info("User login successful for user ID: {}", userId);
            user = userService.getUserById(userId);
            userMenu();
        } else {
            System.out.println("\n❌ Login failed. User not found or invalid credentials. ❌\n");
            LOGGER.warn("User login failed for user ID: {}", userId);
        }
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,userdb,pass);
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
            String query=String.format("select * from %s where username= %s AND password= %s",table_name,userId,password);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()){
                System.out.print(rs.getString("username")+" ");
                System.out.print(rs.getString("password")+" ");
                System.out.println(rs.getString("address"));
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void registerUser() {
        String name=getValidInput("Enter Name: ");
        String address=getValidInput("Enter Address: ");
        String password=getValidInput("Enter Password: ");
        userService.createUser(name, address, password);
        System.out.println("User created successfully!");
        LOGGER.info("User created successfully with name: {}", name);
        try{
                Class.forName("org.postgresql.Driver");
                conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,userdb,pass);
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
            String query=String.format("insert into %s values('%s','%s','%s');",table_name,name,password,address);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row Inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void userMenu() {
        while (true) {
            displayUserMenu();
            int choice = Integer.parseInt(getValidInput("\nEnter choice: "));
            handleUserMenuChoice(choice);
            if (choice == 5) {
                System.out.println("Logging out.");
                LOGGER.info("User logged out.");
                user = null; // Clear user session
                return;
            }
        }
    }

    public void displayUserMenu() {
        System.out.println("\n👨‍💼 User Menu:");
        System.out.println("1. Show Bill History");
        System.out.println("2. Pay Bill");
        System.out.println("3. Generate Bill");
        System.out.println("4. View Unpaid Bills");
        System.out.println("5. Logout");
    }

    private void handleUserMenuChoice(int choice) {
        switch (choice) {
            case 1:
                showBillHistory();
                break;
            case 2:
                payBill();
                break;
            case 3:
                generateBill();
                break;
            case 4:
                viewUnpaidBills();
                break;
            case 5:
                return; // Return from this method to exit userMenu loop
            default:
                System.out.println("\n❌ Invalid choice. Please try again. ❌\n");
                LOGGER.warn("Invalid choice selected in user menu: {}", choice);
        }
    }

    private void showBillHistory() {
        System.out.println("\n💸 Bill History:");
        billingService.viewBillHistory(user.getUserId()).forEach(System.out::println);
        LOGGER.info("Displayed bill history for user ID: {}", user.getUserId());
        String table_name="billdata";
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,userdb,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }

        }catch (Exception e){
            System.out.println(e);
        }
        try {
            String query=String.format("select * from %s where userid= %s",table_name,user.getUserId());
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

    private void payBill() {
        String  paymentMethod;
        String billId =getValidInput("Enter Bill ID to pay: ");
        System.out.print("Enter payment method (CREDIT_CARD, DEBIT_CARD, UPI): ");
        paymentMethod = scanner.nextLine().trim();
        String table_name="billdata";
        try {
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            billingService.payBill(user.getUserId(), billId, method); // Pass payment method
            System.out.println("\n Bill paid successfully!\n");
            LOGGER.info("Bill paid successfully for user ID: {} with Bill ID: {} and Payment Method: {}", user.getUserId(), billId, method);
            try{
                Class.forName("org.postgresql.Driver");
                conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,userdb,pass);
                if(conn!=null){
                    System.out.println("Connection Established");
                }
                else{
                    System.out.println("Connection Failed");
                }
    
            }catch (Exception e){
                System.out.println(e);
            }
            try {
                String query=String.format("update %s set paid='%s' where userid='%s' AND billid='%s' ",table_name,"true",user.getUserId(),billId);
                statement=conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("Data Updated");
            }catch (Exception e){
                System.out.println(e);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid payment method. Please enter a valid payment method ❌.");
        } catch (Exception e) {
            System.out.println("Failed to pay bill: " + e.getMessage());
            LOGGER.error("Failed to pay bill for user ID: {} with Bill ID: {}", user.getUserId(), billId, e);
        }
    }

    public void generateBill() {
        try {
            double amount = Double.parseDouble(getValidInput("\nEnter Bill amount:"));
            billingService.generateBill(user.getUserId(), amount);
            System.out.println(" \n Bill generated successfully! \n");
            LOGGER.info("Bill generated successfully for user ID: {} with amount: {}", user.getUserId(), amount);
        } catch (java.util.InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter a valid numeric amount. ❌");
            LOGGER.error("Invalid input for bill amount: {}", e.getMessage());
            scanner.nextLine(); // Consume the invalid input to avoid an infinite loop
        }
    }

    public void viewUnpaidBills() {
        System.out.println("Unpaid Bills:");
        billingService.viewUnpaidBills(user.getUserId()).forEach(System.out::println);
        LOGGER.info("Displayed unpaid bills for user ID: {}", user.getUserId());
        String table_name="billdata";
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,userdb,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }

        }catch (Exception e){
            System.out.println(e);
        }
        try {
            String query=String.format("select * from %s where userid= %s AND paid='%s';",table_name,user.getUserId(),"notpaid");
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
