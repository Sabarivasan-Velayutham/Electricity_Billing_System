package org.example.services;

import org.example.BillHistory;

import java.util.List;

public interface UserManagement
{
    List<BillHistory> getBillHistory();
    void viewBillHistory();
    void generateBill(String customerName, String customerId);
}