package com.aurionpro.bankAccountProject;

import java.sql.Connection;

public class TransactionService {
   
	private Connection con;

    public TransactionService(Connection con) {
        this.con = con;
    }

    public void displayTransactionHistory(int userId, int accountNo) {
        TransactionHistory.displayTransactionHistory(userId, accountNo);
    }
}


