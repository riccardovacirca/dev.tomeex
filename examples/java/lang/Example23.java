/**
 * Example23: JUnit 5 Testing Framework
 * 
 * Demonstrates JUnit 5 (Jupiter) testing framework for Java applications.
 * JUnit is the most widely used testing framework in Java, providing
 * annotations, assertions, and lifecycle management for unit tests.
 * 
 * Key Features:
 * - Annotations for test methods and lifecycle hooks (@Test, @BeforeEach, etc.)
 * - Rich assertion library with descriptive error messages  
 * - Parameterized tests for testing multiple inputs
 * - Test lifecycle management (setup/teardown)
 * - Nested test classes for organized test suites
 * 
 * Common Use Cases:
 * - Unit testing individual methods and classes
 * - Integration testing with external dependencies
 * - API endpoint testing with HTTP clients
 * - Database testing with test containers
 * - Parameterized testing for multiple scenarios
 * 
 * Dependencies required (Maven):
 * <dependency>
 *   <groupId>org.junit.jupiter</groupId>
 *   <artifactId>junit-jupiter</artifactId>
 *   <version>5.10.2</version>
 *   <scope>test</scope>
 * </dependency>
 */

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;
import java.time.Duration;

public class Example23 {
  
  public static void main(String[] args) {
    System.out.println("=== JUnit 5 Testing Framework Examples ===\n");
    System.out.println("This example demonstrates JUnit 5 test structure.");
    System.out.println("To run tests, use: mvn test or your IDE's test runner\n");
    
    // Demo class functionality
    Calculator calc = new Calculator();
    System.out.printf("Calculator demo: 5 + 3 = %d%n", calc.add(5, 3));
    System.out.printf("Calculator demo: 10 / 2 = %d%n", calc.divide(10, 2));
    
    UserService userService = new UserService();
    System.out.printf("UserService demo: User count = %d%n", userService.getUserCount());
    
    System.out.println("\nRun 'mvn test' to execute the JUnit test cases!");
  }
  
  /**
   * Calculator class for testing demonstration
   * Simple arithmetic operations with validation
   */
  static class Calculator {
    
    public int add(int a, int b) {
      return a + b;
    }
    
    public int subtract(int a, int b) {
      return a - b;
    }
    
    public int multiply(int a, int b) {
      return a * b;
    }
    
    public int divide(int a, int b) {
      if (b == 0) {
        throw new IllegalArgumentException("Division by zero not allowed");
      }
      return a / b;
    }
    
    public double sqrt(double value) {
      if (value < 0) {
        throw new IllegalArgumentException("Cannot calculate square root of negative number");
      }
      return Math.sqrt(value);
    }
    
    public boolean isPrime(int number) {
      if (number <= 1) return false;
      if (number <= 3) return true;
      if (number % 2 == 0 || number % 3 == 0) return false;
      
      for (int i = 5; i * i <= number; i += 6) {
        if (number % i == 0 || number % (i + 2) == 0) {
          return false;
        }
      }
      return true;
    }
  }
  
  /**
   * UserService class for testing demonstration
   * Simulates user management operations
   */
  static class UserService {
    
    private List<String> users = new ArrayList<>();
    
    public void addUser(String username) {
      if (username == null || username.trim().isEmpty()) {
        throw new IllegalArgumentException("Username cannot be null or empty");
      }
      if (users.contains(username)) {
        throw new IllegalStateException("User already exists: " + username);
      }
      users.add(username);
    }
    
    public boolean removeUser(String username) {
      return users.remove(username);
    }
    
    public int getUserCount() {
      return users.size();
    }
    
    public boolean userExists(String username) {
      return users.contains(username);
    }
    
    public List<String> getAllUsers() {
      return new ArrayList<>(users);
    }
    
    public void clearUsers() {
      users.clear();
    }
  }
  
  /**
   * Test class demonstrating JUnit 5 features
   * This would typically be in src/test/java directory
   */
  @DisplayName("Calculator Tests")
  static class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
      calculator = new Calculator();
      System.out.println("Setting up test - calculator initialized");
    }
    
    @AfterEach  
    void tearDown() {
      System.out.println("Cleaning up after test");
    }
    
    @Test
    @DisplayName("Addition should work correctly")
    void testAddition() {
      assertEquals(8, calculator.add(5, 3), "5 + 3 should equal 8");
      assertEquals(0, calculator.add(-1, 1), "-1 + 1 should equal 0");
      assertEquals(-2, calculator.add(-5, 3), "-5 + 3 should equal -2");
    }
    
    @Test
    @DisplayName("Division should work correctly")
    void testDivision() {
      assertEquals(2, calculator.divide(6, 3), "6 / 3 should equal 2");
      assertEquals(-2, calculator.divide(-6, 3), "-6 / 3 should equal -2");
    }
    
    @Test
    @DisplayName("Division by zero should throw exception")
    void testDivisionByZero() {
      IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> calculator.divide(10, 0),
        "Division by zero should throw IllegalArgumentException"
      );
      assertTrue(exception.getMessage().contains("Division by zero"));
    }
    
    @Test
    @DisplayName("Square root should work for positive numbers")
    void testSquareRoot() {
      assertEquals(3.0, calculator.sqrt(9.0), 0.001, "sqrt(9) should equal 3");
      assertEquals(0.0, calculator.sqrt(0.0), 0.001, "sqrt(0) should equal 0");
    }
    
    @Test
    @DisplayName("Square root of negative number should throw exception")
    void testSquareRootNegative() {
      assertThrows(
        IllegalArgumentException.class,
        () -> calculator.sqrt(-1.0),
        "Square root of negative number should throw exception"
      );
    }
    
    @ParameterizedTest
    @DisplayName("Prime number detection should work correctly")
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23})
    void testPrimeNumbers(int number) {
      assertTrue(calculator.isPrime(number), number + " should be prime");
    }
    
    @ParameterizedTest
    @DisplayName("Non-prime number detection should work correctly")
    @ValueSource(ints = {1, 4, 6, 8, 9, 10, 12, 14, 15, 16})
    void testNonPrimeNumbers(int number) {
      assertFalse(calculator.isPrime(number), number + " should not be prime");
    }
    
    @ParameterizedTest
    @DisplayName("Multiple calculations should work correctly")
    @CsvSource({
      "1, 2, 3",
      "5, 7, 12", 
      "-1, 1, 0",
      "0, 0, 0"
    })
    void testAdditionWithCsv(int a, int b, int expected) {
      assertEquals(expected, calculator.add(a, b), 
        String.format("%d + %d should equal %d", a, b, expected));
    }
    
    @Test
    @DisplayName("Performance test - calculation should complete quickly")
    void testPerformance() {
      assertTimeout(Duration.ofMillis(100), () -> {
        for (int i = 0; i < 1000; i++) {
          calculator.add(i, i + 1);
        }
      }, "1000 additions should complete within 100ms");
    }
  }
  
  /**
   * Nested test class demonstrating test organization
   */
  @DisplayName("UserService Tests")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  static class UserServiceTest {
    
    private UserService userService;
    
    @BeforeAll
    void setUpAll() {
      System.out.println("Setting up UserService test suite");
    }
    
    @AfterAll
    void tearDownAll() {
      System.out.println("Cleaning up UserService test suite");
    }
    
    @BeforeEach
    void setUp() {
      userService = new UserService();
    }
    
    @Nested
    @DisplayName("User Addition Tests")
    class UserAdditionTest {
      
      @Test
      @DisplayName("Should add valid user successfully")
      void testAddValidUser() {
        userService.addUser("john_doe");
        
        assertEquals(1, userService.getUserCount(), "User count should be 1");
        assertTrue(userService.userExists("john_doe"), "User should exist");
      }
      
      @Test
      @DisplayName("Should throw exception for null username")
      void testAddNullUser() {
        assertThrows(
          IllegalArgumentException.class,
          () -> userService.addUser(null),
          "Adding null user should throw exception"
        );
      }
      
      @Test
      @DisplayName("Should throw exception for empty username")
      void testAddEmptyUser() {
        assertThrows(
          IllegalArgumentException.class,
          () -> userService.addUser(""),
          "Adding empty user should throw exception"
        );
      }
      
      @Test
      @DisplayName("Should throw exception for duplicate user")
      void testAddDuplicateUser() {
        userService.addUser("john_doe");
        
        assertThrows(
          IllegalStateException.class,
          () -> userService.addUser("john_doe"),
          "Adding duplicate user should throw exception"
        );
      }
    }
    
    @Nested
    @DisplayName("User Removal Tests")
    class UserRemovalTest {
      
      @Test
      @DisplayName("Should remove existing user successfully")
      void testRemoveExistingUser() {
        userService.addUser("jane_doe");
        
        assertTrue(userService.removeUser("jane_doe"), "Remove should return true");
        assertFalse(userService.userExists("jane_doe"), "User should no longer exist");
        assertEquals(0, userService.getUserCount(), "User count should be 0");
      }
      
      @Test
      @DisplayName("Should return false when removing non-existent user")
      void testRemoveNonExistentUser() {
        assertFalse(userService.removeUser("non_existent"), 
          "Remove non-existent user should return false");
      }
    }
    
    @Test
    @DisplayName("Should return correct user list")
    void testGetAllUsers() {
      userService.addUser("user1");
      userService.addUser("user2");
      userService.addUser("user3");
      
      List<String> users = userService.getAllUsers();
      
      assertEquals(3, users.size(), "Should have 3 users");
      assertTrue(users.contains("user1"), "Should contain user1");
      assertTrue(users.contains("user2"), "Should contain user2");
      assertTrue(users.contains("user3"), "Should contain user3");
    }
    
    @Test
    @DisplayName("Should clear all users")
    void testClearUsers() {
      userService.addUser("user1");
      userService.addUser("user2");
      
      assertEquals(2, userService.getUserCount(), "Should have 2 users initially");
      
      userService.clearUsers();
      
      assertEquals(0, userService.getUserCount(), "Should have 0 users after clear");
      assertTrue(userService.getAllUsers().isEmpty(), "User list should be empty");
    }
  }
}