/**
 * Example21: File I/O and Resource Reading
 * 
 * This example demonstrates basic file input/output operations in Java,
 * including reading from resources, handling streams, and text processing.
 * 
 * Key Concepts:
 * - InputStream for reading data streams
 * - BufferedReader for efficient text reading
 * - getResourceAsStream() for reading from classpath resources
 * - try-with-resources for automatic resource management
 * - Exception handling for I/O operations
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Example21 {
    
    public static void main(String[] args) {
        System.out.println("=== Example21: File I/O and Resource Reading ===\n");
        
        // Demonstrate reading from resources
        demonstrateResourceReading();
        
        // Demonstrate text processing
        demonstrateTextProcessing();
        
        // Demonstrate error handling
        demonstrateErrorHandling();
    }
    
    /**
     * Demonstrates how to read files from the classpath resources
     */
    public static void demonstrateResourceReading() {
        System.out.println("--- Reading from Resources ---");
        
        // Create a simple text content to simulate a resource file
        String simulatedContent = "Line 1: Hello World\nLine 2: Java I/O\nLine 3: Resource Reading";
        System.out.println("Simulated resource content:");
        System.out.println(simulatedContent);
        System.out.println();
        
        // Note: In real applications, you would use:
        // InputStream stream = MyClass.class.getClassLoader().getResourceAsStream("path/to/file.txt");
        System.out.println("Real usage pattern:");
        System.out.println("InputStream stream = MyClass.class.getClassLoader().getResourceAsStream(\"path/to/file.txt\");");
        System.out.println();
    }
    
    /**
     * Demonstrates text processing and line-by-line reading
     */
    public static void demonstrateTextProcessing() {
        System.out.println("--- Text Processing ---");
        
        // Simulate reading lines from a text source
        String[] lines = {
            "-- This is a comment",
            "CREATE TABLE users (id INT, name VARCHAR(50));",
            "",
            "-- Another comment", 
            "INSERT INTO users VALUES (1, 'John');"
        };
        
        System.out.println("Processing SQL-like content:");
        System.out.println("Original lines:");
        for (int i = 0; i < lines.length; i++) {
            System.out.println((i + 1) + ": " + lines[i]);
        }
        
        System.out.println("\nFiltered content (no comments, no empty lines):");
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            String trimmedLine = line.trim();
            // Skip comments and empty lines
            if (!trimmedLine.startsWith("--") && !trimmedLine.isEmpty()) {
                content.append(trimmedLine).append("\n");
                System.out.println("Added: " + trimmedLine);
            }
        }
        
        System.out.println("\nFinal processed content:");
        System.out.println(content.toString());
    }
    
    /**
     * Demonstrates proper error handling for I/O operations
     */
    public static void demonstrateErrorHandling() {
        System.out.println("--- Error Handling ---");
        
        // Simulate reading from a resource (this would normally be a real file)
        try {
            String result = readResourceSafely("simulated-file.txt");
            System.out.println("Successfully read: " + result.length() + " characters");
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates safe resource reading with proper exception handling
     * This method simulates reading from classpath resources
     */
    public static String readResourceSafely(String resourcePath) throws IOException {
        System.out.println("Attempting to read resource: " + resourcePath);
        
        // Simulate the resource reading process
        if (resourcePath.equals("simulated-file.txt")) {
            // For demonstration, return simulated content
            return "This is simulated file content\nWith multiple lines\nAnd some data";
        } else {
            // Simulate file not found
            throw new IOException("Resource not found: " + resourcePath);
        }
    }
    
    /**
     * TEMPLATE: Real resource reading method (for reference)
     * This shows how you would actually read from classpath resources
     */
    public static String readFromResourceTemplate(String resourcePath) throws IOException {
        // Get the resource as an InputStream
        InputStream inputStream = Example21.class.getClassLoader().getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        
        // Use try-with-resources for automatic cleanup
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder content = new StringBuilder();
            String line;
            
            // Read line by line
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            return content.toString();
        }
        // BufferedReader and InputStream are automatically closed here
    }
}

/**
 * Expected Output:
 * 
 * === Example21: File I/O and Resource Reading ===
 * 
 * --- Reading from Resources ---
 * Simulated resource content:
 * Line 1: Hello World
 * Line 2: Java I/O
 * Line 3: Resource Reading
 * 
 * Real usage pattern:
 * InputStream stream = MyClass.class.getClassLoader().getResourceAsStream("path/to/file.txt");
 * 
 * --- Text Processing ---
 * Processing SQL-like content:
 * Original lines:
 * 1: -- This is a comment
 * 2: CREATE TABLE users (id INT, name VARCHAR(50));
 * 3: 
 * 4: -- Another comment
 * 5: INSERT INTO users VALUES (1, 'John');
 * 
 * Filtered content (no comments, no empty lines):
 * Added: CREATE TABLE users (id INT, name VARCHAR(50));
 * Added: INSERT INTO users VALUES (1, 'John');
 * 
 * Final processed content:
 * CREATE TABLE users (id INT, name VARCHAR(50));
 * INSERT INTO users VALUES (1, 'John');
 * 
 * --- Error Handling ---
 * Attempting to read resource: simulated-file.txt
 * Successfully read: 69 characters
 */