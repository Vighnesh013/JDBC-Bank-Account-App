package com.aurionpro.bankAccountProject;

import java.sql.*;

public class AccountService {
    private Connection con;

    public AccountService(Connection con) {
        this.con = con;
    }

    public void addNewAccount(int userId, int accountNo, String name, double initialDeposit) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM bank_account WHERE account_no = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, accountNo);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Account number already exists.");
                }
            }
        }

        String insertSql = "INSERT INTO bank_account (account_no, name, balance, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
            insertStmt.setInt(1, accountNo);
            insertStmt.setString(2, name);
            insertStmt.setDouble(3, initialDeposit);
            insertStmt.setInt(4, userId);
            insertStmt.executeUpdate();
            con.commit();
        }
        TransactionFile.logTransaction(userId, accountNo, "Account Creation", initialDeposit, "Success");
    }

    public double checkBalance(int userId, int accountNo) throws SQLException {
        String sql = "SELECT balance FROM bank_account WHERE account_no = ? AND user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, accountNo);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        throw new SQLException("Account not found or unauthorized.");
    }

    public void depositMoney(int userId, int accountNo, double amount) throws SQLException {
        String sql = "UPDATE bank_account SET balance = balance + ? WHERE account_no = ? AND user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, accountNo);
            stmt.setInt(3, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                con.commit();
                TransactionFile.logTransaction(userId, accountNo, "Deposit", amount, "Success");
            } else {
                throw new SQLException("Account not found or unauthorized.");
            }
        }
    }

    public void withdrawMoney(int userId, int accountNo, double amount) throws SQLException {
        String checkSql = "SELECT balance FROM bank_account WHERE account_no = ? AND user_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, accountNo);
            checkStmt.setInt(2, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    if (balance >= amount) {
                        String updateSql = "UPDATE bank_account SET balance = balance - ? WHERE account_no = ?";
                        try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                            updateStmt.setDouble(1, amount);
                            updateStmt.setInt(2, accountNo);
                            updateStmt.executeUpdate();
                            con.commit();
                            TransactionFile.logTransaction(userId, accountNo, "Withdrawal", amount, "Success");
                            return;
                        }
                    } else {
                        throw new SQLException("Insufficient balance.");
                    }
                }
            }
        }
        throw new SQLException("Account not found or unauthorized.");
    }

    public void transferMoney(int senderUserId, int senderAccountNo, int recipientAccountNo, double amount) throws SQLException {
        String senderSql = "SELECT balance FROM bank_account WHERE account_no = ? AND user_id = ?";
        String recipientSql = "SELECT user_id FROM bank_account WHERE account_no = ?";

        int recipientUserId = -1;

        try (PreparedStatement senderStmt = con.prepareStatement(senderSql);
             PreparedStatement recipientStmt = con.prepareStatement(recipientSql)) {

            senderStmt.setInt(1, senderAccountNo);
            senderStmt.setInt(2, senderUserId);
            try (ResultSet senderRs = senderStmt.executeQuery()) {
                if (!senderRs.next()) throw new SQLException("Sender account not found or unauthorized.");

                double senderBalance = senderRs.getDouble("balance");
                if (senderBalance < amount) throw new SQLException("Insufficient funds.");
            }

            recipientStmt.setInt(1, recipientAccountNo);
            try (ResultSet recipientRs = recipientStmt.executeQuery()) {
                if (recipientRs.next()) {
                    recipientUserId = recipientRs.getInt("user_id");
                } else {
                    throw new SQLException("Recipient account not found.");
                }
            }

            String withdrawSql = "UPDATE bank_account SET balance = balance - ? WHERE account_no = ?";
            String depositSql = "UPDATE bank_account SET balance = balance + ? WHERE account_no = ?";

            try (PreparedStatement withdrawStmt = con.prepareStatement(withdrawSql);
                 PreparedStatement depositStmt = con.prepareStatement(depositSql)) {

                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setInt(2, senderAccountNo);
                withdrawStmt.executeUpdate();

                depositStmt.setDouble(1, amount);
                depositStmt.setInt(2, recipientAccountNo);
                depositStmt.executeUpdate();

                con.commit();

                TransactionFile.logTransaction(senderUserId, senderAccountNo, "Transfer Sent", amount, "Success");
                TransactionFile.logTransaction(recipientUserId, recipientAccountNo, "Transfer Received", amount, "Success");
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}

