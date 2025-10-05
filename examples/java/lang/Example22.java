/**
 * Example22: Try-with-resources Pattern
 * 
 * Demonstrates the try-with-resources statement for automatic resource management.
 * This pattern ensures that resources implementing AutoCloseable are automatically 
 * closed when the try block exits, preventing resource leaks.
 * 
 * Key Features:
 * - Automatic resource closing (no explicit close() needed)
 * - Multiple resources in single try statement
 * - Exception suppression when both try block and close() throw
 * - Works with any class implementing AutoCloseable or Closeable
 * 
 * Common Use Cases:
 * - File operations (FileInputStream, BufferedReader, etc.)
 * - Database operations (Connection, Statement, ResultSet)
 * - Network operations (Socket, ServerSocket)
 * - Any custom resource that implements AutoCloseable
 */

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class Example22 {
    
    public static void main(String[] args) {
        System.out.println("=== Try-with-resources Examples ===\n");
        
        // Example 1: Single resource - file reading
        demonstrateSingleResource();
        
        // Example 2: Multiple resources in one try statement
        demonstrateMultipleResources();
        
        // Example 3: Custom AutoCloseable resource
        demonstrateCustomResource();
        
        // Example 4: Exception handling with try-with-resources
        demonstrateExceptionHandling();
        
        System.out.println("\n=== Examples completed ===");
    }
    
    /**
     * Example 1: Basic try-with-resources with single resource
     * Shows automatic closing of BufferedReader
     */
    private static void demonstrateSingleResource() {
        System.out.println("1. Single Resource Example:");
        
        // Create a temporary file for demonstration
        try {
            Path tempFile = Files.createTempFile("example", ".txt");
            Files.write(tempFile, "Hello World!\nSecond line\nThird line".getBytes());
            
            // Try-with-resources: BufferedReader automatically closed
            try (BufferedReader reader = Files.newBufferedReader(tempFile)) {
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    System.out.printf("   Line %d: %s%n", lineNumber++, line);
                }
            } // reader.close() called automatically here
            
            // Cleanup
            Files.deleteIfExists(tempFile);
            
        } catch (IOException e) {
            System.out.printf("   Error: %s%n", e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Example 2: Multiple resources in single try-with-resources
     * Shows semicolon-separated resource declarations
     */
    private static void demonstrateMultipleResources() {
        System.out.println("2. Multiple Resources Example:");
        
        try {
            Path sourceFile = Files.createTempFile("source", ".txt");
            Path targetFile = Files.createTempFile("target", ".txt");
            Files.write(sourceFile, "Content to copy".getBytes());
            
            // Multiple resources separated by semicolon
            try (BufferedReader reader = Files.newBufferedReader(sourceFile);
                 BufferedWriter writer = Files.newBufferedWriter(targetFile)) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("   File copied successfully");
                
            } // Both reader and writer closed automatically (in reverse order)
            
            // Verify copy worked
            String copiedContent = Files.readString(targetFile);
            System.out.printf("   Copied content: %s%n", copiedContent.trim());
            
            // Cleanup
            Files.deleteIfExists(sourceFile);
            Files.deleteIfExists(targetFile);
            
        } catch (IOException e) {
            System.out.printf("   Error: %s%n", e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Example 3: Custom AutoCloseable resource
     * Shows how to create and use custom resources with try-with-resources
     */
    private static void demonstrateCustomResource() {
        System.out.println("3. Custom AutoCloseable Resource Example:");
        
        // Using custom resource with try-with-resources
        try (DatabaseConnection dbConn = new DatabaseConnection("localhost:5432")) {
            dbConn.executeQuery("SELECT * FROM users");
            System.out.println("   Query executed successfully");
        } // dbConn.close() called automatically
        
        System.out.println();
    }
    
    /**
     * Example 4: Exception handling with try-with-resources
     * Shows suppressed exceptions when both try block and close() throw
     */
    private static void demonstrateExceptionHandling() {
        System.out.println("4. Exception Handling Example:");
        
        try (ProblematicResource resource = new ProblematicResource()) {
            System.out.println("   Using problematic resource...");
            resource.doSomething();
        } catch (Exception e) {
            System.out.printf("   Main exception: %s%n", e.getMessage());
            
            // Check for suppressed exceptions (from close() method)
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                System.out.printf("   Suppressed exception: %s%n", t.getMessage());
            }
        }
        
        System.out.println();
    }
    
    /**
     * Custom AutoCloseable implementation for demonstration
     * Simulates a database connection that needs proper closing
     */
    static class DatabaseConnection implements AutoCloseable {
        private String connectionString;
        private boolean isOpen;
        
        public DatabaseConnection(String connectionString) {
            this.connectionString = connectionString;
            this.isOpen = true;
            System.out.printf("   Database connection opened to: %s%n", connectionString);
        }
        
        public void executeQuery(String query) {
            if (!isOpen) {
                throw new IllegalStateException("Connection is closed");
            }
            System.out.printf("   Executing query: %s%n", query);
        }
        
        @Override
        public void close() {
            if (isOpen) {
                System.out.println("   Database connection closed");
                isOpen = false;
            }
        }
    }
    
    /**
     * Resource that throws exceptions in both normal operation and close()
     * Used to demonstrate suppressed exceptions
     */
    static class ProblematicResource implements AutoCloseable {
        
        public void doSomething() throws Exception {
            throw new Exception("Something went wrong in doSomething()");
        }
        
        @Override
        public void close() throws Exception {
            throw new Exception("Error during close()");
        }
    }
}