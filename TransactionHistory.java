package com.aurionpro.bankAccountProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TransactionHistory {

	public static void displayTransactionHistory(int userId, int accountNo) {
	    System.out.println("Reading transaction history for userId: " + userId + ", accountNo: " + accountNo);
	    try (BufferedReader reader = new BufferedReader(new FileReader("transaction_history.txt"))) {
	        String line;
	        boolean found = false;

	        System.out.println("\nTransaction History for Account: " + accountNo);
	        while ((line = reader.readLine()) != null) {
	           
	            String[] fields = line.split(",");
	            if (fields.length < 6) continue;

	            int logUserId = Integer.parseInt(fields[0]);
	            int logAccountNo = Integer.parseInt(fields[1]);

	            if (logUserId == userId && logAccountNo == accountNo) {
	                String type = fields[2];
	                double amount = Double.parseDouble(fields[3]);
	                String date = fields[4];
	                String status = fields[5];

	                System.out.printf("Type: %-15s | Amount: ₹%-8.2f | Date: %-20s | Status: %s%n",
	                        type, amount, date, status);
	                found = true;
	            }
	        }

	        if (!found) {
	            System.out.println("\nNo transactions found for this account.");
	        }

	    } catch (IOException e) {
	        System.out.println("Error reading transaction history: " + e.getMessage());
	    }
	}
}


