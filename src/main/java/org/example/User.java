package org.example;

import org.example.services.UserManagement;

import java.util.ArrayList;
import java.util.List;

class BillHistoryList extends Exception{
    public String getMessage()
    {
        return "Bill History is empty";
    }
}
public class User implements UserManagement {
    private List<BillHistory> billHistories;

    public User() {
        this.billHistories = new ArrayList<>();
    }

    public List<BillHistory> getBillHistory()
    {
        return this.billHistories;
    }

    public void viewBillHistory() {
        System.out.println("Viewing bill history for user.");
        System.out.println("Bill History for User:");
        try{
            if(billHistories.size()==0)
            {
                throw new BillHistoryList();
            }
            for (BillHistory history : billHistories) {
                history.printBillHistory();
            }
        }
        catch(BillHistoryList e)
        {
            System.out.print(e.getMessage());
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
