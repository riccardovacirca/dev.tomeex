/* *****************************************************************************
 * Example03 - Prepared Statements (Secure Queries)
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp postgresql-42.7.3.jar Example03.java
 * Run: java -cp .:postgresql-42.7.3.jar Example03
 * 
 * Demo: Prepared statements for secure database operations
 * *****************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Example03 {
    
    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://tomee-dev-postgres:5432/devdb");
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "devpass123";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Ensures users table exists
     */
    public static void setupDatabase() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(150) UNIQUE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Database setup complete");
        }
    }
    
    /**
     * Insert user with prepared statement (returns generated ID)
     */
    public static long insertUser(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("id");
                    System.out.println("✅ User inserted: " + name + " (ID: " + userId + ")");
                    return userId;
                }
                throw new SQLException("Failed to insert user");
            }
        }
    }
    
    /**
     * Find user by ID
     */
    public static void findUserById(long id) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n🔍 User found:");
                    System.out.println("  ID: " + rs.getLong("id"));
                    System.out.println("  Name: " + rs.getString("name"));
                    System.out.println("  Email: " + rs.getString("email"));
                    System.out.println("  Created: " + rs.getTimestamp("created_at"));
                } else {
                    System.out.println("❌ User with ID " + id + " not found");
                }
            }
        }
    }
    
    /**
     * Search users by email pattern
     */
    public static void findUsersByEmailPattern(String pattern) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE email ILIKE ? ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + pattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n🔍 Users matching email pattern '" + pattern + "':");
                System.out.printf("%-5s %-15s %-20s%n", "ID", "Name", "Email");
                System.out.println("-".repeat(45));
                
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("%-5d %-15s %-20s%n",
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
                
                if (!found) {
                    System.out.println("  No users found");
                }
            }
        }
    }
    
    /**
     * Update user email
     */
    public static boolean updateUserEmail(long id, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newEmail);
            stmt.setLong(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User email updated: ID " + id + " -> " + newEmail);
                return true;
            } else {
                System.out.println("❌ No user found with ID " + id);
                return false;
            }
        }
    }
    
    /**
     * Delete user by ID
     */
    public static boolean deleteUser(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("🗑️ User deleted: ID " + id);
                return true;
            } else {
                System.out.println("❌ No user found with ID " + id);
                return false;
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Prepared Statements Examples ===");
            
            // Setup database
            setupDatabase();
            
            // Insert test users
            System.out.println("\n📝 Inserting users:");
            long user1 = insertUser("Alice Johnson", "alice@example.com");
            long user2 = insertUser("Charlie Brown", "charlie@example.com");
            long user3 = insertUser("Diana Prince", "diana@company.com");
            
            // Find user by ID
            findUserById(user1);
            
            // Search by email pattern
            findUsersByEmailPattern("example");
            findUsersByEmailPattern("company");
            
            // Update user email
            updateUserEmail(user2, "charlie.brown@newcompany.com");
            
            // Find updated user
            findUserById(user2);
            
            // Try to delete a user
            deleteUser(user3);
            
            // Search again to verify deletion
            findUsersByEmailPattern("company");
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            System.err.println("Make sure PostgreSQL container is running on port 9293");
        }
    }
}