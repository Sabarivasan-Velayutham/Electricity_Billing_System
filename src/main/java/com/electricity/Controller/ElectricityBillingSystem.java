package com.electricity.Controller;

import com.electricity.service.AdminService;
import com.electricity.service.UserService;
import com.electricity.service.BillingService;
import com.electricity.service.BeanFactory;

import java.sql.SQLException;
import java.util.Scanner;

public class ElectricityBillingSystem {
    private AdminService adminService;
    private BillingService billingService;
    private UserService userService;
    private Scanner scanner;
    private boolean useDB;

    public ElectricityBillingSystem(boolean useDB) {
        if (useDB){
            this.adminService = (AdminService) BeanFactory.getBean("adminServiceDB");
            this.billingService = (BillingService) BeanFactory.getBean("billingServiceDB");
            this.userService = (UserService) BeanFactory.getBean("userServiceDB");
        }
        else {
            this.adminService = (AdminService) BeanFactory.getBean("adminServiceMM");
            this.billingService = (BillingService) BeanFactory.getBean("billingServiceMM");
            this.userService = (UserService) BeanFactory.getBean("userServiceMM");
        }
        this.scanner = new Scanner(System.in);
        this.useDB = useDB;
    }

    public void run() throws SQLException {
        System.out.println("\nWelcome to the Electricity Billing System!");

        while (true) {
            System.out.println("\nLogin page :");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Create New User");
            System.out.println("4. Exit");
            int choice = Integer.parseInt(getValidInput("\nEnter choice: "));

            switch (choice) {
                case 1:
                    AdminCLIManager adminCLIManager = new AdminCLIManager(useDB);
                    adminCLIManager.start();
                    break;
                case 2:
                case 3:
                    UserCLIManager userCLIManager = new UserCLIManager(useDB);
                    userCLIManager.start();
                    break;
                case 4:
                    System.out.println("\n Exiting Electricity Billing System. \n");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private String getValidInput(String message) {
        String input;
        do {
            System.out.print(message);
            try {
                input = scanner.nextLine().trim();
                if (input.isEmpty() || input.equals("0")) {
                    System.out.println("❌ Input cannot be empty or zero. Please enter a valid input. ❌\n");
                } else if (!isValidInput(input)) {
                    System.out.println("❌ Invalid input. Please enter a number between 1 and 4. ❌\n");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number. ❌\n");
                input = "";
                scanner.nextLine();
            }
        } while (input.isEmpty() || input.equals("0") || !isValidInput(input));
        return input;
    }

    private boolean isValidInput(String input) {
        try {
            int number = Integer.parseInt(input);
            return number >= 1 && number <= 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


