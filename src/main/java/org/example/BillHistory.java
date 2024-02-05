package org.example;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class BillHistory implements Serializable {
    private static final Logger logger = Logger.getLogger(BillHistory.class.getName());

    private String customerName;
    private String customerId;
    private Date paymentDate;
    private double paidAmount;

    BillHistory(String customerName, String customerId, double paidAmount) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.paidAmount = paidAmount;

        // Set paymentDate to the current date and time
        this.paymentDate = new Date();
    }

    public void printBillHistory() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Logger statements
        logger.info("Customer: " + customerName + ", ID: " + customerId);
        logger.info("Amount to Pay: " + paidAmount);
        logger.info("Payment Date: " + dateFormat.format(paymentDate));

        // Print statements
        System.out.println("Customer: " + customerName + ", ID: " + customerId);
        System.out.println("Amount to Pay: " + paidAmount);
        System.out.println("Payment Date: " + dateFormat.format(paymentDate));
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public double getPaidAmount() {
        return this.paidAmount;
    }
}
