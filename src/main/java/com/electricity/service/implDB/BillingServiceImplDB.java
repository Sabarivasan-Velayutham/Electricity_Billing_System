
package com.electricity.service.implDB;

import com.electricity.Model.Bill;
import com.electricity.Controller.DataBaseConnectionManager;
import com.electricity.Exceptions.BillingException;
import com.electricity.service.BillingService;
import com.electricity.service.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillingServiceImplDB implements BillingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingServiceImplDB.class);

    @Override
    public void generateBill(String userId, double amount) {
        String billId = UUID.randomUUID().toString();
        insertBillDataIntoDB(userId, billId, amount);
        LOGGER.info("Bill generated successfully for user ID: {}", userId);
    }

    @Override
    public void addBillForUser(String userId, double amount) {
        String billId = UUID.randomUUID().toString();
        insertBillDataIntoDB(userId, billId, amount);
        LOGGER.info("Bill added successfully for user ID: {}", userId);
    }

    private void insertBillDataIntoDB(String userId, String billId, double amount) {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getBillTableName();
            String query = "INSERT INTO " + tableName + " (userid, billid, billamount, paid) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, billId);
                preparedStatement.setDouble(3, amount);
                preparedStatement.setBoolean(4, false); // 'paid' initially set to false

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error inserting bill data into the database: {}", e.getMessage());
        }
    }

    @Override
    public List<Bill> viewBillHistory(String userId) {
        List<Bill> userBills = new ArrayList<>();
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getBillTableName();
            String query = "SELECT * FROM " + tableName + " WHERE userid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String billId = resultSet.getString("billid");
                        double billAmount = resultSet.getDouble("billamount");
                        boolean paid = resultSet.getBoolean("paid");
                        userBills.add(new Bill(billId, userId, billAmount, paid));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving bill history from the database: {}", e.getMessage());
        }
        return userBills;
    }

    @Override
    public List<Bill> viewUnpaidBills(String userId) {
        List<Bill> unpaidBills = new ArrayList<>();
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getBillTableName();
            String query = "SELECT * FROM " + tableName + " WHERE userid = ? AND paid = 'false'";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String billId = resultSet.getString("billid");
                        double billAmount = resultSet.getDouble("billamount");
                        unpaidBills.add(new Bill(billId, userId, billAmount, false));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving unpaid bills from the database: {}", e.getMessage());
        }
        return unpaidBills;
    }

    @Override
    public void payBill(String userId, String billId, PaymentMethod paymentMethod) throws BillingException {
        try (Connection connection = DataBaseConnectionManager.getConnection()) {
            String tableName = DataBaseConnectionManager.getBillTableName();
            String query = "UPDATE " + tableName + " SET paid = true WHERE userid = ? AND billid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, billId);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    LOGGER.info("Bill paid successfully: userId={}, billId={}", userId, billId);
                } else {
                    throw new BillingException("Bill not found or already paid");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating bill status in the database: {}", e.getMessage());
            throw new BillingException("Error paying the bill");
        }
    }
}

