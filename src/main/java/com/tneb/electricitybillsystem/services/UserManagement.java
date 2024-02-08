package com.tneb.electricitybillsystem.services;

import com.tneb.electricitybillsystem.BillHistory;

import java.util.List;

public interface UserManagement
{
    List<BillHistory> getBillHistory();
    void viewBillHistory();
    void generateBill(String customerName, String customerId);
}