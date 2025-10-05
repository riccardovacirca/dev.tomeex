/* *****************************************************************************
 * Example04 - Transaction Management
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp postgresql-42.7.3.jar Example04.java
 * Run: java -cp .:postgresql-42.7.3.jar Example04
 * 
 * Demo: Database transactions with commit/rollback
 * *****************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Example04 {
    
    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://tomee-dev-postgres:5432/devdb");
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "devpass123";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Creates accounts table for transaction demo
     */
    public static void setupDatabase() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS accounts (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String insertTestDataSQL = """
            INSERT INTO accounts (name, balance) VALUES 
            ('John Doe', 1000.00),
            ('Jane Smith', 500.00),
            ('Bob Wilson', 750.00)
            ON CONFLICT DO NOTHING
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create table first, then clear existing data for clean demo
            stmt.executeUpdate(createTableSQL);
            stmt.executeUpdate("DELETE FROM accounts");
            stmt.executeUpdate(insertTestDataSQL);
            
            System.out.println("✅ Database setup complete with test accounts");
        }
    }
    
    /**
     * Shows current account balances
     */
    public static void showAccountBalances() throws SQLException {
        String sql = "SELECT id, name, balance FROM accounts ORDER BY id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\n💰 Current Account Balances:");
            System.out.printf("%-5s %-15s %-12s%n", "ID", "Name", "Balance");
            System.out.println("-".repeat(35));
            
            while (rs.next()) {
                System.out.printf("%-5d %-15s $%-10.2f%n",
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("balance")
                );
            }
        }
    }
    
    /**
     * Successful money transfer (transaction commits)
     */
    public static void transferMoneySuccess(long fromAccountId, long toAccountId, double amount) throws SQLException {
        String debitSQL = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String creditSQL = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        
        System.out.println("\n💸 Attempting transfer: $" + amount + " from ID " + fromAccountId + " to ID " + toAccountId);
        
        try (Connection conn = getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Debit from source account
                try (PreparedStatement debitStmt = conn.prepareStatement(debitSQL)) {
                    debitStmt.setDouble(1, amount);
                    debitStmt.setLong(2, fromAccountId);
                    debitStmt.setDouble(3, amount); // Ensure sufficient funds
                    
                    int rowsAffected = debitStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient funds or account not found");
                    }
                    System.out.println("✅ Debited $" + amount + " from account " + fromAccountId);
                }
                
                // Credit to destination account
                try (PreparedStatement creditStmt = conn.prepareStatement(creditSQL)) {
                    creditStmt.setDouble(1, amount);
                    creditStmt.setLong(2, toAccountId);
                    
                    int rowsAffected = creditStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Destination account not found");
                    }
                    System.out.println("✅ Credited $" + amount + " to account " + toAccountId);
                }
                
                // All operations successful - commit transaction
                conn.commit();
                System.out.println("✅ Transfer completed successfully!");
                
            } catch (SQLException e) {
                // Error occurred - rollback transaction
                conn.rollback();
                System.err.println("❌ Transfer failed: " + e.getMessage());
                System.err.println("🔄 Transaction rolled back");
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Failed money transfer (insufficient funds - transaction rolls back)
     */
    public static void transferMoneyFail(long fromAccountId, long toAccountId, double amount) throws SQLException {
        String debitSQL = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String creditSQL = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        
        System.out.println("\n💸 Attempting transfer: $" + amount + " from ID " + fromAccountId + " to ID " + toAccountId);
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Try to debit excessive amount (will fail)
                try (PreparedStatement debitStmt = conn.prepareStatement(debitSQL)) {
                    debitStmt.setDouble(1, amount);
                    debitStmt.setLong(2, fromAccountId);
                    debitStmt.setDouble(3, amount);
                    
                    int rowsAffected = debitStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient funds or account not found");
                    }
                }
                
                // This won't be reached due to insufficient funds
                try (PreparedStatement creditStmt = conn.prepareStatement(creditSQL)) {
                    creditStmt.setDouble(1, amount);
                    creditStmt.setLong(2, toAccountId);
                    creditStmt.executeUpdate();
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("❌ Transfer failed: " + e.getMessage());
                System.err.println("🔄 Transaction rolled back - no changes made");
                // Don't rethrow - this is expected for demo
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Transaction Management Examples ===");
            
            // Setup test data
            setupDatabase();
            
            // Show initial balances
            showAccountBalances();
            
            // Successful transfer
            transferMoneySuccess(1, 2, 100.00);
            showAccountBalances();
            
            // Failed transfer (insufficient funds)
            transferMoneyFail(2, 3, 10000.00);
            showAccountBalances();
            
            // Another successful transfer
            transferMoneySuccess(3, 1, 250.00);
            showAccountBalances();
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            System.err.println("Make sure PostgreSQL container is running and accessible");
        }
    }
}