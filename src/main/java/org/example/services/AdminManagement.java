package org.example.services;

import org.example.ElectricityBill;

import java.io.IOException;

public interface AdminManagement {
    void addCustomer(ElectricityBill customer);
    void viewCustomerList();
    void saveCustomerListToFile() throws IOException;
    void viewMissedPayments();

}
