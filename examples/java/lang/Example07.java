/* *****************************************************************************
 * Example 07 - Classes and Objects
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example07.java
 * Run: java Example07
 * ---
 * Hello, Alice!
 * Coordinates: (0, 0)
 * Coordinates: (3, 0)
 * Coordinates: (3, 4)
 * Sum calculated: 12
 * *****************************************************************************
*/

class BasicClass {
  private String name;
  public BasicClass(String name) {
    this.name = name;
  }
  public String greet() {
    return "Hello, " + this.name + "!";
  }
}

class ConstructorOverload {
  private int x;
  private int y;
  public ConstructorOverload() {
    this(0, 0);
  }
  public ConstructorOverload(int x) {
    this(x, 0);
  }
  public ConstructorOverload(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public String toString() {
    return "Coordinates: (" + this.x + ", " + this.y + ")";
  }
}

class Calculator {
  public int add(int a, int b) {
    return a + b;
  }
}

class ObjectInteraction {
  private String buff = null;
  public ObjectInteraction() {
    Calculator calc = new Calculator();
    int sum = calc.add(5, 7);
    this.buff = "Sum calculated: " + sum;
  }
  public String toString() {
    return this.buff;
  }
}

public class Example07 {
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new BasicClass("Alice").greet());
    System.out.printf("%s\n", new ConstructorOverload());
    System.out.printf("%s\n", new ConstructorOverload(3));
    System.out.printf("%s\n", new ConstructorOverload(3, 4));
    System.out.printf("%s\n", new ObjectInteraction());
    System.out.printf("\n");
  }
}