package com.aurionpro.bankAccountProject;

import java.sql.*;

public class UserService {
    private Connection con;
    private int loggedInUserId = -1;

    public UserService(Connection con) {
        this.con = con;
    }

    public boolean login(String username, String passwordInput) throws SQLException {
        String query = "SELECT id, password FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    if (dbPassword.equals(passwordInput)) {
                        loggedInUserId = rs.getInt("id");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void signUp(String username, String passwordInput) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Username already exists.");
                }
            }
        }

        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, passwordInput);
            insertStmt.executeUpdate();
            con.commit();
        }
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}

