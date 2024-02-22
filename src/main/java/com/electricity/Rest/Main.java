package com.electricity.Rest;

import com.electricity.Controller.ElectricityBillingSystem;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Do you want to use the Database Implementation? (yes/no)");

        Scanner scanner = new Scanner(System.in);
        String userChoice = scanner.nextLine().toLowerCase();

        ElectricityBillingSystem system;
        if (userChoice.equals("yes")) {
            system = new ElectricityBillingSystem(true);
        } else {
            system = new ElectricityBillingSystem(false);
        }

        system.run();
    }
}
