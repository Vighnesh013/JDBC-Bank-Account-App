package com.aurionpro.bankAccountProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TransactionHistory {


    public static void displayTransactionHistory(int accountNo) {
        try (BufferedReader reader = new BufferedReader(new FileReader("transaction_history.txt"))) {
            String line;
            boolean foundTransactions = false;

            System.out.println("\nTransaction History for Account: " + accountNo);
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int loggedAccountNo = Integer.parseInt(fields[0]);
                
               
                if (loggedAccountNo == accountNo) {
                    String transactionType = fields[1];
                    double amount = Double.parseDouble(fields[2]);
                    String date = fields[3];
                    String status = fields[4];

                   
                    System.out.println("\nType: " + transactionType + " | Amount: ₹" + amount + " | Date and Time : " + date + " | Status: " + status);
                    foundTransactions = true;
                }
            }

            if (!foundTransactions) {
                System.out.println("\nNo transactions found for this account.");
            }

        } catch (IOException e) {
            System.out.println("\nError while reading transaction history: " + e.getMessage());
        }
    }
}

