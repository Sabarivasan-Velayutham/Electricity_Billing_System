package org.example;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    static {
        try {
            // Setting up a file handler to log to a file named "application.log"
            FileHandler fileHandler = new FileHandler("application.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.print("Welcome to the Electricity Bill System!\n");

        // Create Admin and Users
        UserManager userManager = new UserManager();
        userManager.createUser("admin", "adminpassword");
        userManager.createUser("user1","user1password");
        userManager.createUser("user2","user2password");
        String choice;
        do {
            System.out.print("1. Admin\n");
            System.out.print("2. User\n");
            System.out.print("3. Exit\n");
            System.out.print("Enter your choice (1, 2, or 3): ");
            choice = scanner.next();
            scanner.nextLine(); // Consume the newline character
            switch (choice) {
                case "1":
                    handleAdminLogin(userManager);
                    break;
                case "2":
                    handleUserLogin(userManager);
                    break;
                case "3":
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        } while (!choice.equals("3"));
    }

    private static void handleUserLogin(UserManager userManager) {
        String username, password;
        do {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            System.out.print("Enter password: ");
            password = scanner.nextLine();

            if (userManager.authenticateUser(username, password)) {
                User.handleUserActions(userManager);
                return;
            } else {
                System.out.println("Invalid user credentials. Please try again.\n");
            }
        } while (true);
    }

    private static void handleAdminLogin(UserManager userManager) {
        String adminUsername, adminPassword;
        do {
            System.out.print("Enter admin username: ");
            adminUsername = scanner.nextLine();
            System.out.print("Enter admin password: ");
            adminPassword = scanner.nextLine();
            if (userManager.authenticateUser(adminUsername, adminPassword)) {
                Admin.handleAdminActions(userManager);
                return;
            } else {
                System.out.println("Invalid admin credentials. Please try again.\n");
            }
        } while (true);
    }

}
