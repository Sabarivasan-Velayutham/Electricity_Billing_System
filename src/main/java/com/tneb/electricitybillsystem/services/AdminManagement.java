package com.tneb.electricitybillsystem.services;

import com.tneb.electricitybillsystem.ElectricityBill;

import java.io.IOException;

public interface AdminManagement {
    void addCustomer(ElectricityBill customer);
    void viewCustomerList();
    void saveCustomerListToFile() throws IOException;
    void viewMissedPayments();

}
