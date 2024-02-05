package org.example;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<BillHistory> billHistories;

    public User() {
        this.billHistories = new ArrayList<>(10);
    }

    public void viewBillHistory() {
        System.out.println("Viewing bill history for user.");
        System.out.println("Bill History for User:");
        for (BillHistory history : billHistories) {
            history.printBillHistory();
        }
    }

    public void payElectricityBill(String billNumber, String paymentMethod) {
        try {
            int index = Integer.parseInt(billNumber) - 1;
            if (index >= 0 && index < billHistories.size()) {
                BillHistory billToPay = billHistories.get(index);
                System.out.println("Paid " + billToPay.getPaidAmount() + " using " + paymentMethod +
                        " for bill " + billNumber + ".");

                // Perform any additional payment-related actions if needed

                // Remove the bill from the list after payment
                billHistories.remove(index);
            } else {
                System.out.println("Invalid bill number entered: " + billNumber);
                System.out.println("Invalid bill number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for bill number. Please enter a number." + e);
            System.out.println("Invalid input for bill number. Please enter a number.");
        }
    }

    public void generateBill(String customerName, String customerId) {
        // Assuming some logic to generate a bill amount (e.g., random or user input)
        double billAmount = 150.0; // Replace this with your logic

        // Create a new BillHistory object and add it to the list
        BillHistory newBill = new BillHistory(customerName, customerId, billAmount);
        billHistories.add(newBill);

        System.out.println("Bill generated for " + customerName + " ID:" + customerId + " Amount:" + billAmount);
        System.out.println("Bill generated for " + customerName + ", ID: " + customerId);
        newBill.printBillHistory();
    }
}
