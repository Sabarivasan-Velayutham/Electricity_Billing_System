package org.example;

import java.util.*;
//class PaymentBillHistoryList extends Exception{
//    public String getMessage()
//    {
//        return "Bill History is empty";
//    }
//}

public class Payment {
    public void payElectricityBill(String billNumber, String paymentMethod,User user) {
        try {
            ArrayList<BillHistory> billHistory=new ArrayList<>();
            billHistory=(ArrayList<BillHistory>) user.getBillHistory();
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
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for bill number. Please enter a number." + e);
            System.out.println("Invalid input for bill number. Please enter a number.");
        }
    }
}
