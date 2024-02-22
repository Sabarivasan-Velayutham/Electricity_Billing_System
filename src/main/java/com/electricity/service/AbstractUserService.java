package com.electricity.service;

import com.electricity.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserService.class);
    protected void appendUserDataToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("userdata.txt", true))) {
            writer.write("User ID: " + user.getUserId() + "\n");
            writer.write("Name: " + user.getName() + "\n");
            writer.write("Address: " + user.getAddress() + "\n");
            writer.write("---------------------------------\n");

            LOGGER.info("User data appended to file: userdata.txt");
        } catch (IOException e) {
            LOGGER.error("Error appending user data to file: {}", e.getMessage());
        }
    }

//    public abstract void createUser(String name, String address, String password, String fileLocation);

    protected abstract void appendUserDataToCustomLocation(User user, String fileLocation);
    protected abstract void appendUserDataToCustomLocation(String userId, String name, String address, String fileLocation);

//    protected abstract void appendUserDataToCustomLocation(String userId, String name, String address, String fileLocation);
}