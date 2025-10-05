/* *****************************************************************************
 * Example05 - Batch Operations and Performance
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp postgresql-42.7.3.jar Example05.java
 * Run: java -cp .:postgresql-42.7.3.jar Example05
 * 
 * Demo: Batch operations for high performance inserts/updates
 * *****************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Example05 {
    
    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://tomee-dev-postgres:5432/devdb");
    private static final String DB_USER = "devuser";
    private static final String DB_PASSWORD = "devpass123";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Simple User class for demo
     */
    static class User {
        public String name;
        public String email;
        public String department;
        
        public User(String name, String email, String department) {
            this.name = name;
            this.email = email;
            this.department = department;
        }
    }
    
    /**
     * Setup test table
     */
    public static void setupDatabase() throws SQLException {
        String dropTableSQL = "DROP TABLE IF EXISTS batch_users";
        String createTableSQL = """
            CREATE TABLE batch_users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(150) UNIQUE NOT NULL,
                department VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(dropTableSQL);
            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Test table created: batch_users");
        }
    }
    
    /**
     * Generate test data
     */
    public static List<User> generateTestUsers(int count) {
        List<User> users = new ArrayList<>();
        String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance"};
        
        for (int i = 1; i <= count; i++) {
            String name = "User" + String.format("%04d", i);
            String email = "user" + i + "@company.com";
            String department = departments[i % departments.length];
            users.add(new User(name, email, department));
        }
        
        return users;
    }
    
    /**
     * Single insert approach (slow for large datasets)
     */
    public static void insertUsersSingle(List<User> users) throws SQLException {
        String sql = "INSERT INTO batch_users (name, email, department) VALUES (?, ?, ?)";
        
        System.out.println("\n📝 Single Insert Method - " + users.size() + " users:");
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (User user : users) {
                stmt.setString(1, user.name);
                stmt.setString(2, user.email);
                stmt.setString(3, user.department);
                stmt.executeUpdate();
            }
            
            long endTime = System.currentTimeMillis();
            System.out.println("✅ Completed in: " + (endTime - startTime) + "ms");
        }
    }
    
    /**
     * Batch insert approach (much faster)
     */
    public static void insertUsersBatch(List<User> users) throws SQLException {
        String sql = "INSERT INTO batch_users (name, email, department) VALUES (?, ?, ?)";
        
        System.out.println("\n⚡ Batch Insert Method - " + users.size() + " users:");
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Add all inserts to batch
            for (User user : users) {
                stmt.setString(1, user.name);
                stmt.setString(2, user.email);
                stmt.setString(3, user.department);
                stmt.addBatch();
            }
            
            // Execute all at once
            int[] results = stmt.executeBatch();
            
            long endTime = System.currentTimeMillis();
            System.out.println("✅ Batch executed: " + results.length + " statements");
            System.out.println("✅ Completed in: " + (endTime - startTime) + "ms");
        }
    }
    
    /**
     * Batch insert with transaction (fastest and safest)
     */
    public static void insertUsersBatchTransaction(List<User> users) throws SQLException {
        String sql = "INSERT INTO batch_users (name, email, department) VALUES (?, ?, ?)";
        
        System.out.println("\n🚀 Batch + Transaction Method - " + users.size() + " users:");
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                // Add all inserts to batch
                for (User user : users) {
                    stmt.setString(1, user.name);
                    stmt.setString(2, user.email);
                    stmt.setString(3, user.department);
                    stmt.addBatch();
                }
                
                // Execute batch
                int[] results = stmt.executeBatch();
                
                // Commit transaction
                conn.commit();
                
                long endTime = System.currentTimeMillis();
                System.out.println("✅ Batch executed: " + results.length + " statements");
                System.out.println("✅ Transaction committed");
                System.out.println("✅ Completed in: " + (endTime - startTime) + "ms");
                
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("❌ Batch failed, rolled back: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Batch update example
     */
    public static void batchUpdateDepartments() throws SQLException {
        String sql = "UPDATE batch_users SET department = ? WHERE name LIKE ?";
        
        System.out.println("\n🔄 Batch Update Example:");
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Update different user groups
            stmt.setString(1, "Senior Engineering");
            stmt.setString(2, "User001%");
            stmt.addBatch();
            
            stmt.setString(1, "Lead Marketing");
            stmt.setString(2, "User002%");
            stmt.addBatch();
            
            stmt.setString(1, "VP Sales");
            stmt.setString(2, "User003%");
            stmt.addBatch();
            
            int[] results = stmt.executeBatch();
            
            long endTime = System.currentTimeMillis();
            int totalUpdated = 0;
            for (int result : results) {
                totalUpdated += result;
            }
            
            System.out.println("✅ Batch updates completed: " + totalUpdated + " rows affected");
            System.out.println("✅ Completed in: " + (endTime - startTime) + "ms");
        }
    }
    
    /**
     * Show statistics
     */
    public static void showStats() throws SQLException {
        String sql = "SELECT department, COUNT(*) as count FROM batch_users GROUP BY department ORDER BY count DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n📊 Department Statistics:");
            System.out.printf("%-20s %s%n", "Department", "Count");
            System.out.println("-".repeat(35));
            
            while (rs.next()) {
                System.out.printf("%-20s %d%n",
                    rs.getString("department"),
                    rs.getInt("count")
                );
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Batch Operations Examples ===");
            
            // Setup
            setupDatabase();
            
            // Performance comparison with smaller dataset first
            System.out.println("\n🔬 Performance Comparison (100 records):");
            List<User> smallUsers = generateTestUsers(100);
            insertUsersSingle(smallUsers);
            
            // Clear and test batch
            setupDatabase();
            insertUsersBatch(smallUsers);
            
            // Clear and test batch with transaction
            setupDatabase();
            insertUsersBatchTransaction(smallUsers);
            
            // Larger dataset demonstration
            System.out.println("\n\n🚀 Large Dataset Test (1000 records):");
            setupDatabase();
            List<User> largeUsers = generateTestUsers(1000);
            insertUsersBatchTransaction(largeUsers);
            
            // Batch updates
            batchUpdateDepartments();
            
            // Show final statistics
            showStats();
            
            System.out.println("\n💡 Key Takeaways:");
            System.out.println("   • Batch operations are much faster than single inserts");
            System.out.println("   • Adding transactions makes batches even more efficient");
            System.out.println("   • Always use batches for bulk operations");
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            System.err.println("Make sure PostgreSQL container is running on port 9293");
        }
    }
}