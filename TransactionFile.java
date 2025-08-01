package com.aurionpro.bankAccountProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionFile {

    public static void logTransaction(int userId, int accountNo, String transactionType, double amount, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transaction_history.txt", true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedDateTime = now.format(formatter);

            String line = userId + "," + accountNo + "," + transactionType + "," + amount + "," + formattedDateTime + "," + status;
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }
}


