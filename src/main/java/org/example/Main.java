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
        printMessage("Welcome to the Electricity Bill System!");

        // Create Admin and Users
        UserManager userManager = new UserManager();
        userManager.createUser("admin", "adminpassword");

        String choice;
        do {
            printMessage("1. Admin");
            printMessage("2. User");
            printMessage("3. Exit");
            printMessage("Enter your choice (1, 2, or 3): ");
            choice = scanner.next();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case "1":
                    handleAdminActions(userManager);
                    break;
                case "2":
                    handleUserActions(userManager);
                    break;
                case "3":
                    printMessage("Exiting program.");
                    break;
                default:
                    printWarning("Invalid choice. Please try again.");
            }
        } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3"));
    }

    private static void handleAdminActions(UserManager userManager) {
        Admin admin = new Admin();
        String adminChoice;
        String adminUsername, adminPassword;
        do {
            printMessage("\nAdmin Menu:");
            printMessage("1. Add Customer and Calculate Bill");
            printMessage("2. View Customer List and Bills");
            printMessage("3. View Customers Who Missed Payments");
            printMessage("4. Save Customer List to File");
            printMessage("5. Log Action");
            printMessage("6. Exit");

            printMessage("Enter your choice: ");
            adminChoice = scanner.next();
            scanner.nextLine(); // Consume the newline character

            switch (adminChoice) {
                case "1":
                    handleAdminAddCustomer(admin);
                    break;
                case "2":
                    admin.viewCustomerList();
                    break;
                case "3":
                    admin.viewMissedPayments();
                    break;
                case "4":
                    admin.saveCustomerListToFile();
                    break;
                case "5":
                    printMessage("Enter action to log: ");
                    String action = scanner.nextLine();
                    admin.logAction(action);
                    break;
                case "6":
                    printMessage("Exiting Admin Menu.");
                    break;
                default:
                    printWarning("Invalid choice. Please try again.");
            }
        } while (!adminChoice.equals("1") && !adminChoice.equals("2") && !adminChoice.equals("3")
            && !adminChoice.equals("4") && !adminChoice.equals("5") && !adminChoice.equals("6"));
    }

    private static void handleAdminAddCustomer(Admin admin) {
        printMessage("Enter customer name: ");
        String customerName = scanner.nextLine();
        printMessage("Enter customer ID: ");
        String customerId = scanner.nextLine();

        ElectricityBill.OnlineElectricityBill customer = new ElectricityBill.OnlineElectricityBill(
                customerName, customerId, "Credit Card");

        admin.addCustomer(customer);
        printMessage("Customer added successfully.");
    }

    private static void handleUserActions(UserManager userManager) {
        User user = new User();
        String userChoice;
        do {
            printMessage("\nUser Menu:");
            printMessage("1. View Bill History");
            printMessage("2. Pay Electricity Bill");
            printMessage("3. Generate Bill");
            printMessage("4. Exit");
            printMessage("Enter your choice: ");
            userChoice = scanner.next();
            scanner.nextLine(); // Consume the newline character

            switch (userChoice) {
                case "1":
                    user.viewBillHistory();
                    break;
                case "2":
                    handleUserPayBill(user);
                    break;
                case "3":
                    handleUserGenerateBill(user);
                    break;
                case "4":
                    printMessage("Exiting User Menu.");
                    break;
                default:
                    printWarning("Invalid choice. Please try again.");
            }
        }while (!userChoice.equals("1") && !userChoice.equals("2") && !userChoice.equals("3")
                && !userChoice.equals("4"));
    }

    private static void handleUserPayBill(User user) {
        printMessage("Enter bill number to pay: ");
        String billNumber = scanner.nextLine();
        printMessage("Enter payment method (UPI, DEBIT, CREDIT, etc.): ");
        String paymentMethod = scanner.nextLine();
        Payment payment = new Payment();
        payment.payElectricityBill(billNumber, paymentMethod, user);
    }

    private static void handleUserGenerateBill(User user) {
        printMessage("Enter customer name: ");
        String customerName = scanner.nextLine();
        printMessage("Enter customer ID: ");
        String customerId = scanner.nextLine();

        user.generateBill(customerName, customerId);
    }

    private static void printMessage(String message) {
        logger.info(message);
        System.out.println(message);
    }

    private static void printWarning(String message) {
        logger.warning(message);
        System.out.println(message);
    }
}
