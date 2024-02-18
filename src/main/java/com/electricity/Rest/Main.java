package com.electricity.Rest;

import com.electricity.Controller.ElectricityBillingSystem;

public class Main {
    public static void main(String[] args) {
        ElectricityBillingSystem system = new ElectricityBillingSystem();
        system.run();
    }
}
