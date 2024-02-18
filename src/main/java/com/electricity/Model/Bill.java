package com.electricity.Model;

import com.electricity.service.PaymentMethod;

import java.util.Objects;

public class Bill {
    private String billId;
    private String userId;
    private double amount;
    private boolean paid;

    private PaymentMethod paymentMethod;

    public Bill(String billId, String userId, double amount) {
        this.billId = billId;
        this.userId = userId;
        this.amount = amount;
        this.paid = false;
    }


    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Double.compare(amount, bill.amount) == 0 && paid == bill.paid && Objects.equals(billId, bill.billId) && Objects.equals(userId, bill.userId);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", paid=" + paid +
                '}';
    }
}

