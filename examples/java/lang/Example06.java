/* *****************************************************************************
 * Example06 - Exception Handling
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example06.java
 * Run: java Example06
 * ---
 * Null pointer caught: NullPointerException
 * Method threw exception: Invalid argument provided
 * Custom exception: Input cannot be empty
 * *****************************************************************************
*/

class BasicException {
  private String buff = null;
  public BasicException() {
    try {
      String str = null;
      int length = str.length();
      this.buff = "String length: " + length;
    } catch (NullPointerException e) {
      this.buff = "Null pointer caught: " + e.getClass().getSimpleName();
    } catch (Exception e) {
      this.buff = "General exception caught: " + e.getClass().getSimpleName();
    }
  }
  public String toString() {
    return this.buff;
  }
}

class ThrowsException {
  private String buff = null;
  public ThrowsException() {
    try {
      this.riskyMethod();
      this.buff = "Method completed successfully";
    } catch (IllegalArgumentException e) {
      this.buff = "Method threw exception: " + e.getMessage();
    }
  }
  private void riskyMethod() throws IllegalArgumentException {
    throw new IllegalArgumentException("Invalid argument provided");
  }
  public String toString() {
    return this.buff;
  }
}

class ValidationException extends Exception {
  public ValidationException(String message) {
    super(message);
  }
}

class CustomException {
  private String buff = null;
  public CustomException() {
    try {
      this.validateInput("");
      this.buff = "Validation passed";
    } catch (ValidationException e) {
      this.buff = "Custom exception: " + e.getMessage();
    }
  }
  private void validateInput(String input) throws ValidationException {
    if (input == null || input.trim().isEmpty()) {
      throw new ValidationException("Input cannot be empty");
    }
  }
  public String toString() {
    return this.buff;
  }
}

public class Example06 {
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new BasicException());
    System.out.printf("%s\n", new ThrowsException());
    System.out.printf("%s\n", new CustomException());
    System.out.printf("\n");
  }
}