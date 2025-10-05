/* *****************************************************************************
 * Example01 - Database Connection Manager
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp postgresql-42.7.3.jar Example01.java
 * Run: java -cp .:postgresql-42.7.3.jar Example01
 * 
 * Demo: Basic database connection and test
 * *****************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Example01 {
    
    // Database configuration for PostgreSQL container
    // Use container name when running from TomEE container, localhost when running from host
    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://tomee-dev-postgres:5432/devdb");
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "devpass123";
    
    /**
     * Ottiene una connessione diretta al database
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Chiude connessione in modo sicuro
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test connessione database
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            System.out.println("Database connection test: " + (isValid ? "SUCCESS" : "FAILED"));
            return isValid;
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===");
        
        // Test basic connection
        if (testConnection()) {
            System.out.println("✅ Connection successful!");
            System.out.println("Database URL: " + DB_URL);
            System.out.println("User: " + DB_USER);
        } else {
            System.out.println("❌ Connection failed!");
            System.out.println("Make sure PostgreSQL container is running on port 9293");
        }
    }
}