package org.example;

import org.example.services.UserManagement;
import java.util.Scanner;
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

    public static void handleUserActions(UserManager userManager) {
        Scanner scanner=new Scanner(System.in);
        User user = new User();
        String userChoice;
        do {
            System.out.print("\nUser Menu:\n");
            System.out.print("1. View Bill History\n");
            System.out.print("2. Pay Electricity Bill\n");
            System.out.print("3. Generate Bill\n");
            System.out.print("4. Exit\n");
            System.out.print("Enter your choice: ");
            userChoice = scanner.next();
            scanner.nextLine(); // Consume the newline character

            switch (userChoice) {
                case "1":
                    user.viewBillHistory();
                    break;
                case "2":
                    user.handleUserPayBill(user);
                    break;
                case "3":
                    user.handleUserGenerateBill(user);
                    break;
                case "4":
                    System.out.print("Exiting User Menu.\n");
                    return ;
                default:
                    System.out.print("Invalid choice. Please try again.\n");
            }
        }while (!userChoice.equals("4"));
        // scanner.close();
    }

    public void viewBillHistory() {
        System.out.println("Viewing bill history for user.\n");
        System.out.println("Bill History for User:\n");
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

        System.out.println("Bill generated for " + customerName + " ID:" + customerId + " Amount:" + billAmount+"\n");
        System.out.println("Bill generated for " + customerName + ", ID: " + customerId+"\n");
        newBill.printBillHistory();
    }

    public void handleUserPayBill(User user) {
        Scanner scanner=new Scanner(System.in);
        System.out.print("Enter your name:");
        String name=scanner.nextLine();
        System.out.print("Enter your id:");
        String id=scanner.nextLine();
        System.out.print("Enter bill number to pay: ");
        String billNumber = scanner.nextLine();
        System.out.print("Enter payment method (UPI, DEBIT, CREDIT, etc.): ");
        String paymentMethod = scanner.nextLine();
        Payment payment = new Payment();
        payment.payElectricityBill(billNumber, paymentMethod, user,name,id);
        // scanner.close();
    }

    public void handleUserGenerateBill(User user) {
        Scanner scanner=new Scanner(System.in);
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        user.generateBill(customerName, customerId);
        // scanner.close();
    }
}
