/* *****************************************************************************
 * Example02 - Simple Query Operations (SELECT)
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp postgresql-42.7.3.jar Example02.java
 * Run: java -cp .:postgresql-42.7.3.jar Example02
 * 
 * Demo: Basic SELECT queries and result processing
 * *****************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Example02 {
    
    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://tomee-dev-postgres:5432/devdb");
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "devpass123";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Creates a simple users table for testing
     */
    public static void createTestTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(150) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String insertDataSQL = """
            INSERT INTO users (name, email) VALUES 
            ('John Doe', 'john@example.com'),
            ('Jane Smith', 'jane@example.com'),
            ('Bob Wilson', 'bob@example.com')
            ON CONFLICT (email) DO NOTHING
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Users table created/verified");
            
            int rowsInserted = stmt.executeUpdate(insertDataSQL);
            System.out.println("📝 Test data inserted: " + rowsInserted + " rows");
        }
    }
    
    /**
     * Selects and displays all users
     */
    public static void getAllUsers() throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users ORDER BY id";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== All Users ===");
            System.out.printf("%-5s %-15s %-20s %-20s%n", "ID", "Name", "Email", "Created");
            System.out.println("-".repeat(65));
            
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-20s %-20s%n",
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toString().substring(0, 19)
                );
            }
        }
    }
    
    /**
     * Counts total number of users
     */
    public static void countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                long total = rs.getLong("total");
                System.out.println("\n📊 Total users: " + total);
            }
        }
    }
    
    /**
     * Checks if a table exists
     */
    public static boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Simple Query Examples ===");
            
            // Create test data
            createTestTable();
            
            // Check if table exists
            boolean exists = tableExists("users");
            System.out.println("🔍 Users table exists: " + exists);
            
            // Count users
            countUsers();
            
            // Display all users
            getAllUsers();
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            System.err.println("Make sure PostgreSQL container is running on port 9293");
        }
    }
}