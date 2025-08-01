package com.aurionpro.bankAccountProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionFile {

    public static void logTransaction(int accountNo, String transactionType, double amount, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transaction_history.txt", true))) {
        	
        	 LocalDateTime now = LocalDateTime.now();
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm");
             String formattedDateTime = now.format(formatter);
           
            writer.write(accountNo + "," + transactionType + "," + amount + "," + formattedDateTime + "," + status);
            writer.newLine();  
        } catch (IOException e) {
            System.out.println("Error while adding transaction: " + e.getMessage());
        }
    }
}

