
package com.electricity.Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionManager {
    private static final String DB_NAME = "electricitybillsystem";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sabari";
    private static final String USER_TABLE_NAME = "userinfo";
    private static final String BILL_TABLE_NAME = "billdata";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, USER, PASSWORD);
    }

    public static String getUserTableName() {
        return USER_TABLE_NAME;
    }

    public static String getBillTableName() {
        return BILL_TABLE_NAME;
    }
}


//package com.electricity.Controller;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DataBaseConnectionManager {
//    private static final String DB_NAME = "electricitybillsystem";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "sabari";
//    private static final String USER_TABLE_NAME = "userinfo";
//    private static final String BILL_TABLE_NAME = "billdata";
//    private static Connection connection;
//
//    // Establish connection
//    static {
//        try {
//            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, USER, PASSWORD);
//            System.out.println("Connection established to "+connection);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() {
//        return connection;
//    }
//
//    public static String getUserTableName() {
//        return USER_TABLE_NAME;
//    }
//
//    public static String getBillTableName() {
//        return BILL_TABLE_NAME;
//    }
//
////    public static void closeConnection() {
////        try {
////            if (connection != null && !connection.isClosed()) {
////                connection.close();
////                System.out.println("Connection closed.");
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
//}
