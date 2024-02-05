package org.example;

import java.io.Serializable;

public abstract class ElectricityBill implements Serializable {

    private static final double DEFAULT_BILL_AMOUNT = 100.0;
    private String customerName;
    private String customerId;
    private double billAmount;

    public ElectricityBill(String customerName, String customerId) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.billAmount = DEFAULT_BILL_AMOUNT;
    }

    public ElectricityBill(String customerName, String customerId, double initialBillAmount) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.billAmount = initialBillAmount;
    }

    public synchronized void calculateBill() {
        // Business logic to calculate electricity bill
        // For simplicity, let's assume a fixed bill amount
        this.billAmount = DEFAULT_BILL_AMOUNT;
        System.out.print("Calculated bill for customer:" + customerName + " ID: " + customerId);
    }

    public double getBillAmount() {
        return billAmount;
    }

    public abstract void printBillDetails();

    static class OnlineElectricityBill extends ElectricityBill {
        private String paymentMethod;
        private boolean paid;

        public OnlineElectricityBill(String customerName, String customerId, String paymentMethod) {
            super(customerName, customerId);
            this.paymentMethod = paymentMethod;
            this.paid = false;
        }

        @Override
        public void printBillDetails() {
            System.out.print("Bill Amount:" + getBillAmount());
            System.out.print("Payment Method:" + paymentMethod);
        }

        public boolean hasPaid() {
            return this.paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getCustomerId() {
        return this.customerId;
    }
}
