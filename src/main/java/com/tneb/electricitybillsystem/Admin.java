package com.tneb.electricitybillsystem;

import com.tneb.electricitybillsystem.services.AdminManagement;
import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class CustomerList extends Exception{
    public String getMessage()
    {
        return "Customer List is Empty";
    }
}

public class Admin implements AdminManagement {
    private List<ElectricityBill> customerList;
    private Logger logger;

    public Admin() {
        customerList = new ArrayList<>();
        logger = Logger.getLogger(Admin.class.getName());
        configureLogger();
    }
    public static void handleAdminActions(UserManager userManager) {
        Scanner scanner=new Scanner(System.in);
        Admin admin = new Admin();
        String adminChoice;
        do {
            System.out.print("\nAdmin Menu:\n");
            System.out.print("1. Add Customer and Calculate Bill\n");
            System.out.print("2. View Customer List and Bills\n");
            System.out.print("3. View Customers Who Missed Payments\n");
            System.out.print("4. Save Customer List to File\n");
            System.out.print("5. Log Action\n");
            System.out.print("6. Exit\n");

            System.out.print("Enter your choice: ");
            adminChoice = scanner.next();
            scanner.nextLine();

            switch (adminChoice) {
                case "1":
                    admin.handleAdminAddCustomer(admin);
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
                    System.out.print("Enter action to log: ");
                    String action = scanner.nextLine();
                    admin.logAction(action);
                    break;
                case "6":
                    System.out.print("Exiting Admin Menu.\n");
                    return ;
                default:
                    System.out.print("Invalid choice. Please try again.\n");
            }
        } while (!adminChoice.equals("6"));
        // scanner.close();
    }
    public void handleAdminAddCustomer(Admin admin) {
        Scanner scanner=new Scanner(System.in);
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        ElectricityBill.OnlineElectricityBill customer = new ElectricityBill.OnlineElectricityBill(
                customerName, customerId, "Credit Card");

        admin.addCustomer(customer);
        System.out.print("Customer added successfully.\n");
        // scanner.close();
    }
    private void configureLogger() {
        try {
            // Create a FileHandler to write log messages to a file
            FileHandler fileHandler = new FileHandler("admin.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            // Add the FileHandler to the logger
            logger.addHandler(fileHandler);

            // Set the logger level to INFO (configurable based on requirements)
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(ElectricityBill customer) {
        customerList.add(customer);

        // Logger statement
        logger.info("Added customer: " + customer.getCustomerName() + ", ID: " + customer.getCustomerId());

        // Print statement
        System.out.println("Added customer: " + customer.getCustomerName() + ", ID: " + customer.getCustomerId());
    }

    public void viewCustomerList() {
        // Logger statement
        logger.info("Viewing customer list:");

        // Print statement
        System.out.println("Viewing customer list:");
        try{
            if(customerList.size()==0)
            {
                throw new CustomerList();
            }
            for (ElectricityBill customer : customerList) {
                // Logger statement
                logger.info("Customer: " + customer.getCustomerName() + ", ID: " + customer.getCustomerId());

                // Print statement
                System.out.println("Customer: " + customer.getCustomerName() + ", ID: " + customer.getCustomerId());
            }
        }
        catch(CustomerList e)
        {
            System.out.println(e.getMessage());
        }
    }


    public void saveCustomerListToFile() {
        String fileName = "customerList.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (ElectricityBill customer : customerList) {
                writer.println("Customer: " + customer.getCustomerName() + ", ID: " + customer.getCustomerId());
            }

            // Logger statement
            logger.info("Customer list saved to file: " + fileName);

            // Print statement
            System.out.println("Customer list saved to file: " + fileName);
        } catch (IOException e) {
            // Logger statement
            logger.log(Level.SEVERE, "Error saving customer list to file", e);

            // Print statement
            System.err.println("Error saving customer list to file: " + e.getMessage());
        }
    }

    // Properties: Logger
    public void logAction(String action) {
        String fileName = "log.properties";

        // Check if the file exists
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                // Create the file if it doesn't exist
                if (file.createNewFile()) {
                    System.out.println("File created: " + fileName);
                } else {
                    System.out.println("Unable to create the file.");
                }
            } catch (IOException e) {
                // Handle exception if unable to create the file
                e.printStackTrace();
            }
        }

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("log.properties")) {
            properties.load(input);
            properties.setProperty("action", action);
            try (OutputStream output = new FileOutputStream("log.properties")) {
                properties.store(output, null);
            }

            // Logger statement
            logger.info("Logged action: " + action);

            // Print statement
            System.out.println("Logged action: " + action);
        } catch (IOException e) {
            // Logger statement
            logger.log(Level.SEVERE, "Error logging action", e);

            // Print statement
            System.err.println("Error logging action: " + e.getMessage());
        }
    }

    public void viewMissedPayments() {
        System.out.println("Viewing customer list:");
        try (BufferedReader reader = new BufferedReader(new FileReader("customerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by ", " to separate customer name and ID
                String[] parts = line.split(", ");
                String customerName = parts[0].split(": ")[1]; // Extract customer name
                String customerId = parts[1].split(": ")[1]; // Extract customer ID
                System.out.println("Customer: " + customerName + ", ID: " + customerId+"\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading customer data from file: " + e.getMessage());
        }
    }
}