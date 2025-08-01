package com.aurionpro.bankAccountProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class BankAccount {
	static Scanner scanner = new Scanner(System.in);
	static String url = "jdbc:mysql://localhost:3306/transaction_demo_new";
	static String user = "root";
	static String password = "vighnesh";

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		try (Connection con = DriverManager.getConnection(url, user, password)) {
			con.setAutoCommit(false);
			BankFacade bank = new BankFacade(con);

			int loggedInUserId = -1;

			while (true) {
				System.out.println("\nWelcome to the Bank Application");
				System.out.println("1: Login");
				System.out.println("2: Sign Up");
				System.out.println("3: Exit");
				System.out.print("Enter choice: ");

				int choice = scanner.nextInt();
				scanner.nextLine();

				if (choice == 1) {
					System.out.print("Enter username: ");
					String username = scanner.nextLine();
					System.out.print("Enter password: ");
					String passwordInput = scanner.nextLine();

					if (bank.login(username, passwordInput)) {
						loggedInUserId = bank.userService.getLoggedInUserId();
						System.out.println("\nLogin successful!");
						showBankMenu(bank, loggedInUserId);
					} else {
						System.out.println("Invalid credentials.");
					}
				} else if (choice == 2) {
					System.out.print("Choose a username: ");
					String username = scanner.nextLine();
					System.out.print("Choose a password: ");
					String passwordInput = scanner.nextLine();
					try {
						bank.signUp(username, passwordInput);
						System.out.println("Sign-up successful! You can now log in.");
					} catch (Exception e) {
						System.out.println("Error during sign-up: " + e.getMessage());
					}
				} else if (choice == 3) {
					System.out.println("Thank you for using the app!");
					break;
				} else {
					System.out.println("Enter a valid choice.");
				}
			}
		}
	}

	private static void showBankMenu(BankFacade bank, int loggedInUserId) throws Exception {
		while (true) {
			System.out.println("\nBank Menu:");
			System.out.println("1: Add new Account");
			System.out.println("2: Check bank balance");
			System.out.println("3: Transfer Money");
			System.out.println("4: Deposit Money");
			System.out.println("5: Withdraw Money");
			System.out.println("6: Check Transaction History");
			System.out.println("7: Logout");

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				System.out.print("Enter last four digits of account number: ");
				int accNo = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter account holder name: ");
				String name = scanner.nextLine();
				System.out.print("Enter initial deposit: ");
				double initDeposit = scanner.nextDouble();
				scanner.nextLine();

				bank.addNewAccount(loggedInUserId, accNo, name, initDeposit);
				System.out.println("Account created successfully!");
				break;
			case 2:
				System.out.print("Enter 4-digit account number: ");
				int accBalance = Integer.parseInt(scanner.nextLine());
				try {
					double balance = bank.checkBalance(loggedInUserId, accBalance);
					System.out.println("Balance: ₹" + balance);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case 3:
				System.out.print("Enter your account number: ");
				int senderAcc = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter recipient account number: ");
				int recipientAcc = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter amount to transfer: ");
				double amount = scanner.nextDouble();
				scanner.nextLine();
				try {
					bank.transferMoney(loggedInUserId, senderAcc, recipientAcc, amount);
					System.out.println("Transfer successful.");
				} catch (Exception e) {
					System.out.println("Transfer failed: " + e.getMessage());
				}
				break;
			case 4:
				System.out.print("Enter 4-digit account number: ");
				int depositAcc = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter amount to deposit: ");
				double depositAmount = scanner.nextDouble();
				scanner.nextLine();
				bank.depositMoney(loggedInUserId, depositAcc, depositAmount);
				System.out.println("Deposit successful.");
				break;
			case 5:
				System.out.print("Enter 4-digit account number: ");
				int withdrawAcc = Integer.parseInt(scanner.nextLine());
				System.out.print("Enter amount to withdraw: ");
				double withdrawAmount = scanner.nextDouble();
				scanner.nextLine();
				try {
					bank.withdrawMoney(loggedInUserId, withdrawAcc, withdrawAmount);
					System.out.println("Withdrawal successful.");
				} catch (Exception e) {
					System.out.println("Withdrawal failed: " + e.getMessage());
				}
				break;
			case 6:
				System.out.print("Enter your account number: ");
				int transAcc = Integer.parseInt(scanner.nextLine());
				bank.displayTransactionHistory(loggedInUserId, transAcc);
				break;
			case 7:
				System.out.println("Logged out successfully.");
				return;
			default:
				System.out.println("Enter a valid choice.");
			}
		}
	}
}
