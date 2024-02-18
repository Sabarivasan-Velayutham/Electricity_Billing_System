package com.electricity.service;

import com.electricity.Model.Bill;
import com.electricity.Exceptions.BillingException;

import java.util.List;

public interface BillingService {
    void generateBill(String userId, double amount);

    void addBillForUser(String userId, double amount);

    List<Bill> viewBillHistory(String userId);

    List<Bill> viewUnpaidBills(String userId);


    void payBill(String userId, String billId, PaymentMethod paymentMethod) throws BillingException;
}
