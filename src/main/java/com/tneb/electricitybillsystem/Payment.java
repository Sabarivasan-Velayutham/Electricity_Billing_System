package com.tneb.electricitybillsystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Payment {
    public void payElectricityBill(String billNumber, String paymentMethod, User user, String name, String id) {
        try {
            // Search for the customer details in the file
            boolean customerFound = searchCustomerInFile(name, id);
            if (customerFound) {
                ArrayList<BillHistory> billHistory = new ArrayList<>();
                billHistory = (ArrayList<BillHistory>) user.getBillHistory();
                int index = Integer.parseInt(billNumber) - 1;
                if (index >= 0 && index < billHistory.size()) {
                    BillHistory billToPay = billHistory.get(index);
                    System.out.println("Paid " + billToPay.getPaidAmount() + " using " + paymentMethod +
                            " for bill " + billNumber + ".");

                    // Perform any additional payment-related actions if needed

                    // Remove the bill from the list after payment
                    billHistory.remove(index);
                } else {
                    System.out.println("Invalid bill number entered: " + billNumber);
                    System.out.println("Invalid bill number. Please try again.");
                }
            } else {
                System.out.println("Customer not found. Please check the name and ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for bill number. Please enter a number." + e);
            System.out.println("Invalid input for bill number. Please enter a number.");
        }
    }

    public  boolean searchCustomerInFile(String name, String id) {
        String fileName = "customerList.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                String customerNameFromFile = parts[0].split(": ")[1];
                String customerIdFromFile = parts[1].split(": ")[1];

                // Compare customer name and ID with provided name and ID
                if (customerNameFromFile.equals(name) && customerIdFromFile.equals(id)) {
                    // If found, remove the line from the file
                    removeLineFromFile(fileName, line);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading customer data from file: " + e.getMessage());
        }

        return false;
    }

    public void removeLineFromFile(String fileName, String lineToRemove) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                // Check if the line matches the line to remove
                if (!line.equals(lineToRemove)) {
                    sb.append(line).append("\n"); // Append the line to the StringBuilder
                }
            }

            reader.close();

            // Write the modified contents back to the same file
            FileWriter writer = new FileWriter(fileName);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("Error removing line from file: " + e.getMessage());
        }
    }
}
