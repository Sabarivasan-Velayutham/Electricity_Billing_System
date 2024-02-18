package com.electricity.service;

import com.electricity.Model.Bill;
import com.electricity.Exceptions.BillingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BillingServiceImpl implements BillingService {
    private Map<String, List<Bill>> userBills;

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingServiceImpl.class);

    public BillingServiceImpl() {
        this.userBills = new HashMap<>();
    }
    String dbname="electricitybillsystem";
    String user="postgres";
    String pass="sabari";

    @Override
    public void generateBill(String userId, double amount) {
        String billId = UUID.randomUUID().toString();
        Bill bill = new Bill(billId, userId, amount);
        List<Bill> userBillList = userBills.getOrDefault(userId, new ArrayList<>());
        userBillList.add(bill);
        userBills.put(userId, userBillList);
        LOGGER.info("Bill generated successfully for user ID: {}", userId);
        String table_name="billdata";
        Statement statement;
        Connection  conn=null;
        String paid="notpaid";
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try{
            String query=String.format("insert into %s values('%s','%s','%.2f','%s');",table_name,userId,billId,amount,paid);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Added");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void addBillForUser(String userId, double amount) {
        List<Bill> bills = userBills.getOrDefault(userId, new ArrayList<>());
        String billId = UUID.randomUUID().toString();
        Bill bill = new Bill(billId, userId, amount);
        bills.add(bill);
        userBills.put(userId, bills);
        LOGGER.info("Bill added successfully for user ID: {}", userId);
        appendBillDataToFile(bill);
        String table_name="billdata";
        Statement statement;
        Connection  conn=null;
        String paid="notpaid";
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }    
        }
        catch (Exception e){
            System.out.println(e);
        }
        try{
            String query=String.format("insert into %s values('%s','%s','%.2f','%s');",table_name,userId,billId,amount,paid);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Added");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void appendBillDataToFile(Bill bill) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("billdata.txt", true))) {
            writer.write("User ID: " + bill.getUserId() + "\n");
            writer.write("Bill ID: " + bill.getBillId() + "\n");
            writer.write("Bill Amount: " + bill.getAmount() + "\n");
            writer.write("---------------------------------\n");

            LOGGER.info("Bill data appended to file: billdata.txt");
        } catch (IOException e) {
            LOGGER.error("Error appending bill data to file: {}", e.getMessage());
        }
    }

    @Override
    public List<Bill> viewBillHistory(String userId) {
        return userBills.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public List<Bill> viewUnpaidBills(String userId) {
        List<Bill> unpaidBills = new ArrayList<>();
        for (Bill bill : userBills.getOrDefault(userId, new ArrayList<>())) {
            if (!bill.isPaid()) {
                unpaidBills.add(bill);
            }
        }
        return unpaidBills;
    }

    @Override
    public void payBill(String userId, String billId, PaymentMethod paymentMethod) throws BillingException {
        boolean billFound = false;
        List<Bill> bills = userBills.getOrDefault(userId, new ArrayList<>());
        for (Bill bill : bills) {
            if (bill.getBillId().equals(billId)) {
                if (!bill.isPaid()) {
                    bill.setPaid(true);
                    bill.setPaymentMethod(paymentMethod); // Set payment method
                    LOGGER.info("Bill paid successfully: {}", bill);
                    billFound = true;
                    // Update userBills map to reflect the payment
                    userBills.put(userId, bills);
                    // Rewrite updated bill data to the file
                    rewriteBillDataToFile();
                    break;
                } else {
                    throw new BillingException("Bill already paid");
                }
            }
        }
        if (!billFound) {
            throw new BillingException("Bill not found");
        }
    }

    private void rewriteBillDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("billdata.txt"))) {
            for (List<Bill> bills : userBills.values()) {
                for (Bill bill : bills) {
                    writer.write("User ID: " + bill.getUserId() + "\n");
                    writer.write("Bill ID: " + bill.getBillId() + "\n");
                    writer.write("Bill Amount: " + bill.getAmount() + "\n");
                    writer.write("Paid: " + bill.isPaid() + "\n");
                    writer.write("---------------------------------\n");
                }
            }
            LOGGER.info("Bill data updated in file: billdata.txt");
        } catch (IOException e) {
            LOGGER.error("Error rewriting bill data to file: {}", e.getMessage());
        }
    }
}
