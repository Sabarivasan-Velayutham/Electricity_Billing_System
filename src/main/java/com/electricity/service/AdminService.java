package com.electricity.service;

import com.electricity.Model.Admin;



public interface AdminService {
    void addUser(String name, String address, String password);

    boolean authenticateUser(String userId, String password);

    Admin getUserById(String userId);

    void addBillForUser(String userId, double billAmount);

}
