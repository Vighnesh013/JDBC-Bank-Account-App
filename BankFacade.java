package com.aurionpro.bankAccountProject;

import java.sql.Connection;
import java.sql.SQLException;

public class BankFacade {
    UserService userService;
    private AccountService accountService;
    private TransactionService transactionService;

    public BankFacade(Connection con) {
        this.userService = new UserService(con);
        this.accountService = new AccountService(con);
        this.transactionService = new TransactionService(con);
    }

    public boolean login(String username, String password) throws SQLException {
        return userService.login(username, password);
    }

    public void signUp(String username, String password) throws SQLException {
        userService.signUp(username, password);
    }

    public void addNewAccount(int userId, int accountNo, String name, double initialDeposit) throws SQLException {
        accountService.addNewAccount(userId, accountNo, name, initialDeposit);
    }

    public double checkBalance(int userId, int accountNo) throws SQLException {
        return accountService.checkBalance(userId, accountNo);
    }

    public void depositMoney(int userId, int accountNo, double amount) throws SQLException {
        accountService.depositMoney(userId, accountNo, amount);
    }

    public void withdrawMoney(int userId, int accountNo, double amount) throws SQLException {
        accountService.withdrawMoney(userId, accountNo, amount);
    }

    public void transferMoney(int senderUserId, int senderAccountNo, int recipientAccountNo, double amount) throws SQLException {
        accountService.transferMoney(senderUserId, senderAccountNo, recipientAccountNo, amount);
    }

    public void displayTransactionHistory(int userId, int accountNo) {
        transactionService.displayTransactionHistory(userId, accountNo);
    }
}

