package com.aurionpro.bankAccountProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BankAccountTest {

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		String url = "jdbc:mysql://localhost:3306/transaction_demo_new";
		String user = "root";
		String password = "vighnesh";

		Connection con = null;

		Class.forName("com.mysql.cj.jdbc.Driver");
		con = DriverManager.getConnection(url, user, password);

		con.setAutoCommit(false);

		while (true) {
			try {
				System.out.println("\nEnter which Operation : ");
				System.out.println(
						"1 : Add new Account \n2 : Check bank balance \n3 : Transfer Money \n4 : Deposit Money \n5 : Withdraw Money \n6 : Check Transaction History \n7 : Exit");

				int choice = scanner.nextInt();
				scanner.nextLine();
				if (choice == 1) {
					int acc = 0;
					while (true) {
						System.out.println("Enter last four digits of account number : ");
						String account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}
						acc = Integer.parseInt(account_number);

						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, acc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							System.out
									.println("\nAccount number already exists. Please enter a different account number.\n");
						} else {
							break;
						}
					}

					System.out.println("Enter account holder name : ");
					String name = scanner.nextLine();
					while (true) {
						if (!name.trim().isEmpty() && name.matches("[a-zA-Z ]+")) {
							break;
						} else {
							System.out.println("Enter valid name : ");
							name = scanner.nextLine();
						}
					}

					double addAmount = 0;
					while (true) {
						try {
							System.out.println("Enter initial amount to create account : ");
							addAmount = scanner.nextDouble();
							if (addAmount <= 0) {
								System.out.println("Enter valid amount : ");
								addAmount = scanner.nextDouble();
							} else {
								break;
							}
						} catch (InputMismatchException e) {
							System.out.println("Enter only digits!");
							scanner.nextLine();
						}
					}

					scanner.nextLine();

					PreparedStatement add = con
							.prepareStatement("INSERT INTO bank_account(account_no, name, balance) VALUES (?, ?, ?)");
					add.setInt(1, acc);
					add.setString(2, name);
					add.setDouble(3, addAmount);

					add.executeUpdate();
					con.commit();

					System.out.println("\nNew Account created successfully!");
					TransactionFile.logTransaction(acc, "Account Creation", addAmount, "Success");

				} else if (choice == 2) {
					if (!isAccountExists(con)) {
					    System.out.println("\nNo accounts added yet!\n");
					    continue; 
					}

					boolean check1 = false;
					int acc = 0;
					while(!check1) {
						System.out.println("Enter last four digits of account number : ");
						String account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}

						acc = Integer.parseInt(account_number);
						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, acc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							check1 = true;
						}
						else {
							System.out.println("Account does not exists! Enter again \n");
						}
					}
					

					String query = "SELECT balance FROM bank_account WHERE account_no = ?";
					PreparedStatement check = con.prepareStatement(query);
					check.setInt(1, acc);

					ResultSet rs = check.executeQuery();

					if (rs.next()) {
						System.out.println("\nYour account balance: " + rs.getDouble("balance"));
					}

					con.commit();

				} else if (choice == 3) {
					
					if (!isAccountExists(con)) {
					    System.out.println("\nNo accounts added yet!\n");
					    continue; 
					}
					
					if (!isTwoAccountExists(con)) {
					    System.out.println("\nOnly one account added yet! Transfer requires Two accounts!\n");
					    continue; 
					}
					
					

					boolean check1 = false;
					int senderAcc = 0;
					while(!check1) {
						System.out.println("Enter SENDERS last four digit of account number : ");
						String account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}
						senderAcc = Integer.parseInt(account_number);
						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, senderAcc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							check1 = true;
						} else {
							System.out.println("SENDERS account does not exists! Enter again \n");
						}
					}
					
					boolean check = false;
					int recieverAcc = 0;
					while(!check) {
						System.out.println("Enter RECIEVERS last four digit of account number : ");
						String account_number1 = scanner.nextLine();
						while (true) {
							if (account_number1.length() == 4 && account_number1.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number1 = scanner.nextLine();
							}
						}
						recieverAcc = Integer.parseInt(account_number1);
						if(recieverAcc == senderAcc) {
							System.out.println("recievers account number is same as senders! Enter again ");
						} else {
							PreparedStatement checkAccount = con
									.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
							checkAccount.setInt(1, recieverAcc);
							ResultSet rs = checkAccount.executeQuery();

							if (rs.next() && rs.getInt(1) > 0) {
								check = true;
							} else {
								System.out.println("Reciever account does not exists! Enter again \n");
							}
						}
						
					}
					
					

					try {
						double amount = 0;
						boolean done = false;
						while(!done) {
							while(true) {
								try {
									System.out.println("Enter amount to transfer : ");
									 amount = scanner.nextDouble();
									
										if (amount <= 0) {
											System.out.println("Enter valid amount : ");
											amount = scanner.nextDouble();

										} else {
											break;
										}
									}catch(InputMismatchException e) {
										System.out.println("Enter only digits!");
										scanner.nextLine();
									}
								}
						
							PreparedStatement balanceCheckStmt = con
									.prepareStatement("SELECT balance FROM bank_account WHERE account_no = ?");
							balanceCheckStmt.setInt(1, senderAcc);
							ResultSet rs1 = balanceCheckStmt.executeQuery();

							if (rs1.next()) {
								double currentBalance = rs1.getDouble("balance");
								if (currentBalance >= amount) {

									PreparedStatement withdrawStmt = con.prepareStatement(
											"UPDATE bank_account SET balance = balance - ? WHERE account_no = ?");
									withdrawStmt.setDouble(1, amount);
									withdrawStmt.setInt(2, senderAcc);
									withdrawStmt.executeUpdate();

									PreparedStatement depositStmt = con.prepareStatement(
											"UPDATE bank_account SET balance = balance + ? WHERE account_no = ?");
									depositStmt.setDouble(1, amount);
									depositStmt.setInt(2, recieverAcc);
									depositStmt.executeUpdate();

									con.commit();
									System.out.println("\nTransaction successful! ₹" + amount + " transferred.");
									done = true;
									TransactionFile.logTransaction(senderAcc, "Money Transferd", amount, "Success");
									TransactionFile.logTransaction(recieverAcc, "Money Recieved", amount, "Success");
								} else {
									System.out.println("\nInsufficient balance!");
									String query = "SELECT balance FROM bank_account WHERE account_no = ?";
									PreparedStatement check4 = con.prepareStatement(query);
									check4.setInt(1, senderAcc);

									ResultSet rs = check4.executeQuery();

									if (rs.next()) {
										System.out.println("\nYour account balance is : " + rs.getDouble("balance"));
										System.out.println();
									} else {
										System.out.println("Account not found.");
									}
									con.rollback();
								}
							}
						}
						 

					} catch (Exception e) {
						System.out.println(" Error: " + e.getMessage());
						try {
							if (con != null) {
								con.rollback();
								System.out.println("\nTransaction rolled back.");
							}
						} catch (SQLException rollbackEx) {
							rollbackEx.printStackTrace();
						}
					} 
				} else if (choice == 4) {
					
					if (!isAccountExists(con)) {
					    System.out.println("\nNo accounts added yet!\n");
					    continue; 
					}
					
					boolean check1 = false;
					int acc = 0;
					while(!check1) {
						System.out.println("Enter last four digits of account number : ");
						String account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}

						acc = Integer.parseInt(account_number);
						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, acc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							check1 = true;
						} else {
							System.out.println("Account does not exists! Enter again \n");
						}
					}
					
					double amount = 0;
					while (true) {
						try {
							System.out.println("Enter amount to deposit : ");
							amount = scanner.nextDouble();
							if (amount <= 0) {
								System.out.println("Enter valid amount : ");
								amount = scanner.nextDouble();

							} else {
								break;
							}
						} catch(InputMismatchException e) {
							System.out.println("Enter only digits!");
							scanner.nextLine();
						}
						
					}
					PreparedStatement deposit = con
							.prepareStatement("UPDATE bank_account SET balance = balance + ? WHERE account_no = ?");
					deposit.setDouble(1, amount);
					deposit.setInt(2, acc);

					deposit.executeUpdate();
					con.commit();
					System.out.println("\nRupees "+amount+" deposited successfully!");
					TransactionFile.logTransaction(acc, "Money Deposited", amount, "Success");

				} else if (choice == 5) {
					
					if (!isAccountExists(con)) {
					    System.out.println("\nNo accounts added yet!\n");
					    continue; 
					}
					
					boolean check1 = false;
					int acc = 0;
					while(!check1) {
						System.out.println("Enter last four digits of Account Number : ");
						String account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}

						acc = Integer.parseInt(account_number);
						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, acc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							check1 = true;
						} else {
							System.out.println("Account does not exists! Enter again \n");
						}
					}
					double amount = 0;
					boolean done = false;
					while(!done) {
						while (true) {
							try {
								System.out.println("Enter amount to Withdraw : ");
								amount = scanner.nextDouble();
								if (amount <= 0) {
									System.out.println("Enter valid amount : ");
									amount = scanner.nextDouble();

								} else {
									break;
								}
							} catch(InputMismatchException e) {
								System.out.println("Enter only digits!");
								scanner.nextLine();
							}
							
						}
						PreparedStatement balanceCheckStmt = con
								.prepareStatement("SELECT balance FROM bank_account WHERE account_no = ?");
						balanceCheckStmt.setInt(1, acc);
						ResultSet rs1 = balanceCheckStmt.executeQuery();

						if (rs1.next()) {
							double currentBalance = rs1.getDouble("balance");
							if (currentBalance >= amount) {

								PreparedStatement withdrawStmt = con.prepareStatement(
										"UPDATE bank_account SET balance = balance - ? WHERE account_no = ?");
								withdrawStmt.setDouble(1, amount);
								withdrawStmt.setInt(2, acc);
								withdrawStmt.executeUpdate();
								done = true;
							} else {
								System.out.println("\nInsufficient balance!");
								String query = "SELECT balance FROM bank_account WHERE account_no = ?";
								PreparedStatement check4 = con.prepareStatement(query);
								check4.setInt(1, acc);

								ResultSet rs = check4.executeQuery();

								if (rs.next()) {
									System.out.println("\nYour account balance is : " + rs.getDouble("balance"));
									System.out.println();
								}
							}
						}
					}
					
					con.commit();
					System.out.println("\nRupees " +amount+" Withdrawed successfully!");
					TransactionFile.logTransaction(acc, "Money Withdrawed", amount, "Success");
				} else if (choice == 6) {
					
					if (!isAccountExists(con)) {
					    System.out.println("\nNo accounts added yet!\n");
					    continue; 
					}
					
					boolean check1 = false;
					int acc = 0;
					String account_number = "";
					while(!check1) {
						System.out.println("Enter last four digits of account number : ");
						account_number = scanner.nextLine();
						while (true) {
							if (account_number.length() == 4 && account_number.matches("\\d{4}")) {
								break;
							} else {
								System.out.println("Enter Valid account number : ");
								account_number = scanner.nextLine();
							}
						}

						acc = Integer.parseInt(account_number);
						PreparedStatement checkAccount = con
								.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE account_no = ?");
						checkAccount.setInt(1, acc);
						ResultSet rs = checkAccount.executeQuery();

						if (rs.next() && rs.getInt(1) > 0) {
							check1 = true;
						} else {
							System.out.println("Account does not exists! Enter again \n");
						}
					}
					acc = Integer.parseInt(account_number);

					TransactionHistory.displayTransactionHistory(acc);
					con.commit();
				} else if (choice == 7) {
					System.out.println("--------------------------Thank You-----------------------");
					System.exit(0);
				} else {
					System.out.println("Enter digit only between 1-7");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input! Enter only digits");
				scanner.nextLine();
			}

		}

	}
	
	public static boolean isAccountExists(Connection con) throws SQLException {
	    PreparedStatement stmt = con.prepareStatement("select count(*) from bank_account");
	    ResultSet rs = stmt.executeQuery();
	    if (rs.next()) {
	        return rs.getInt(1) > 0;
	    }
	    return false;
	}
	
	public static boolean isTwoAccountExists(Connection con) throws SQLException {
	    PreparedStatement stmt = con.prepareStatement("select count(*) from bank_account");
	    ResultSet rs = stmt.executeQuery();
	    if (rs.next()) {
	        return rs.getInt(1) > 1;
	    }
	    return false;
	}


}

