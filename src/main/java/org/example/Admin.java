package org.example;

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

public class Admin {
    private List<ElectricityBill> customerList;
    private Logger logger;

    public Admin() {
        customerList = new ArrayList<>();
        logger = Logger.getLogger(Admin.class.getName());
        configureLogger();
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
        // Logger statement
        logger.info("Customers who missed payments:");

        // Print statement
        System.out.println("Customers who missed payments:");
        try{
            if(customerList.size()==0)
            {
                throw new CustomerList();
            }
            for (ElectricityBill customer : customerList) {
                if (customer instanceof ElectricityBill.OnlineElectricityBill) {
                    ElectricityBill.OnlineElectricityBill onlineCustomer = (ElectricityBill.OnlineElectricityBill) customer;

                    if (!onlineCustomer.hasPaid()) {
                        // Logger statement
                        logger.info("Customer: " + onlineCustomer.getCustomerName() + ", ID: " + onlineCustomer.getCustomerId());

                        // Print statement
                        System.out.println("Customer: " + onlineCustomer.getCustomerName() + ", ID: " + onlineCustomer.getCustomerId());
                    }
                }
            }
        }
        catch(CustomerList e)
        {
            System.out.println(e.getMessage());
        }
    }
}