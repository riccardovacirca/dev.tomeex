<!-- ======================================================================= -->

# Java Tutorial

<!-- ======================================================================= -->

## Table of Contents

- [Simple HelloWorld Message](#simple-helloworld-message)
- [Primitive Types and Wrappers](#primitive-types-and-wrappers)
- [Arrays and Arrays Utilities](#arrays-and-arrays-utilities)
- [Wrapper Class Methods](#wrapper-class-methods)
- [Code Blocks](#code-blocks)
- [Exception Handling](#exception-handling)
- [Classes and Objects](#classes-and-objects)
- [Inheritance](#inheritance)
- [Static Members](#static-members)
- [Interfaces](#interfaces)
- [Packages and Imports](#packages-and-imports)
- [Enums](#enums)
- [Collections: List](#collections-list)
- [Collections: Map](#collections-map)
- [Annotations: Basic Usage](#annotations-basic-usage)
- [Records](#records)
- [Memory Management](#memory-management)
- [Event Listeners](#event-listeners)
- [Lambda Expressions](#lambda-expressions)
- [Application Lifecycle Threading](#application-lifecycle-threading)
- [File I/O and Resource Reading](#file-io-and-resource-reading)
- [Try-with-resources Pattern](#try-with-resources-pattern)
- [JUnit 5 Testing Framework](#junit-5-testing-framework)
- [JSON Processing](#json-processing)

<!-- ======================================================================= -->

## Simple HelloWorld Message

Source file: `examples/java/Example01.java`

This example demonstrates the fundamental concepts of Java programming: class definition, object creation, and basic output. It shows how to create a simple class with a private attribute, constructor, and string representation method. The program creates a HelloWorld object and displays its message using the automatic toString() method invocation in printf statements.

### Class Declaration

Basic class definition with a private instance variable to store the message content.

```java
class HelloWorld
{
  private String buff = null;
```

Defines a class named HelloWorld. The class is a template for creating objects.

### Private Attribute

Declaration of an instance variable with private access modifier for encapsulation.

```java
private String buff = null;
```

Declares a private instance variable that stores the message. Only methods of this class can access it.

### Constructor

Default constructor that initializes the object with a greeting message.

```java
public HelloWorld() {
  this.buff = "Hello, World!";
}
```

The constructor is called automatically when creating a new object with `new`. It initializes the buff attribute.

### toString() Method

Override of the toString() method to provide custom string representation.

```java
public String toString() {
  return this.buff;
}
```

Override of the toString() method inherited from Object. Returns the string representation of the object.

### Main Class

The public class that serves as the entry point for the Java application.

```java
public class Example01
{
```

The public class containing the main method. The name must match the filename.

### Main Method

Standard main method signature where program execution begins.

```java
public static void main(String[] args) {
```

Entry point of the program. The JVM looks for this method to start execution.

### Object Creation and Output

Demonstrates object instantiation and formatted output with automatic toString() invocation.

```java
System.out.printf("\n");
System.out.printf("%s\n", new HelloWorld());
System.out.printf("\n");
```

Creates a new HelloWorld object and prints it. The printf automatically calls toString() on the object.

### Complete HelloWorld Example

Full source code combining all the components into a working Java program.

```java
/** HelloWorld class */
class HelloWorld {
  private String buff = null;
  
  public HelloWorld() {
    this.buff = "Hello, World!";
  }
  
  public String toString() {
    return this.buff;
  }
}

public class Example01 {
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new HelloWorld());
    System.out.printf("\n");
  }
}
```

Complete program demonstrating basic class creation, object instantiation, and output.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Primitive Types and Wrappers

Source file: `examples/java/Example02.java`

This example explores Java's primitive data types and their corresponding wrapper classes. It demonstrates the difference between primitive types (char, int, double) and their object wrappers (Character, Integer, Double). The code shows variable declaration, initialization, and formatting for both primitive and wrapper types, illustrating autoboxing and unboxing concepts. Each class formats and displays values using String.format() to show identical behavior between primitives and wrappers.

### CharType Class Declaration

Class designed to showcase both primitive char and Character wrapper usage.

```java
class CharType
{
  private String buff = null;
```

Defines a class that demonstrates character type usage. Uses a buffer to store formatted output.

### CharType Constructor - Primitive Variables

Demonstrates primitive char variable declaration and initialization with character literals.

```java
char x, y, z;
x = 'A';
y = 'B';
z = 'C';
this.buff = String.format("x = %c, y = %c, z = %c", x, y, z);
```

### CharType Constructor - Wrapper Variables

Shows Character wrapper creation with autoboxing from primitive literals.

```java
Character x = 'A', y = 'B', z = 'C';
this.buff += String.format("x = %c, y = %c, z = %c", x, y, z);
```

### IntType Class - Primitive and Wrapper

Complete class comparing primitive int with Integer wrapper behavior.

```java
class IntType {
  private String buff = null;

  public IntType() {
    int x, y, z;
    x = 1;
    y = 2;
    z = 3;
    this.buff = String.format("x = %d, y = %d, z = %d", x, y, z);
    
    this.buff += "\n";
    Integer a = 1, b = 2, c = 3;
    this.buff += String.format("x = %d, y = %d, z = %d", a, b, c);
  }

  public String toString() {
    return this.buff;
  }
}
```

### DoubleType Class - Primitive and Wrapper

Complete class comparing primitive double with Double wrapper using decimal formatting.

```java
class DoubleType {
  private String buff = null;

  public DoubleType() {
    double x, y, z;
    x = 1.1;
    y = 2.2;
    z = 3.3;
    this.buff = String.format("x = %.1f, y = %.1f, z = %.1f", x, y, z);
    
    this.buff += "\n";
    Double a = 1.1, b = 2.2, c = 3.3;
    this.buff += String.format("x = %.1f, y = %.1f, z = %.1f", a, b, c);
  }

  public String toString() {
    return this.buff;
  }
}
```

### Expected Output

```
x = A, y = B, z = C
x = A, y = B, z = C
x = 1, y = 2, z = 3
x = 1, y = 2, z = 3
x = 1.1, y = 2.2, z = 3.3
x = 1.1, y = 2.2, z = 3.3
```

Shows the output comparing primitive types with their corresponding wrapper classes.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Arrays and Arrays Utilities

Source file: `examples/java/Example03.java`

This example covers Java array creation, manipulation, and utility methods. It demonstrates different ways to declare and initialize arrays, both with primitive types and wrapper classes. The code explores the java.util.Arrays utility class, showing essential operations like sorting, searching, copying, comparing, and filling arrays. Each class illustrates specific array operations with practical examples of array element access, modification, and various utility method applications.

### CharArray Class - Primitive Arrays

Shows two array creation methods: explicit size with element assignment and direct initialization.

```java
class CharArray {
  private String buff = null;

  public CharArray() {
    char[] x = new char[3];
    x[0] = 'A';
    x[1] = 'B';
    x[2] = 'C';
    this.buff = String.format("[%c, %c, %c]", x[0], x[1], x[2]);
    
    this.buff += "\n";
    Character[] y = {'A', 'B', 'C'};
    this.buff += String.format("[%c, %c, %c]", y[0], y[1], y[2]);
  }

  public String toString() {
    return this.buff;
  }
}
```

### IntArray Class - Array Creation

Demonstrates integer array creation with primitive and wrapper types using different initialization approaches.

```java
class IntArray {
  private String buff = null;

  public IntArray() {
    int[] x = new int[3];
    x[0] = 10;
    x[1] = 20;
    x[2] = 30;
    this.buff = String.format("[%d, %d, %d]", x[0], x[1], x[2]);
    
    this.buff += "\n";
    Integer[] y = {10, 20, 30};
    this.buff += String.format("[%d, %d, %d]", y[0], y[1], y[2]);
  }

  public String toString() {
    return this.buff;
  }
}
```

### ArraysUtils Class - Utility Methods

Showcases essential Arrays utility methods for sorting, searching, copying, and display formatting.

```java
class ArraysUtils {
  private String buff = null;

  public ArraysUtils() {
    int[] x = {30, 10, 20};
    this.buff = "Original: " + java.util.Arrays.toString(x) + "\n";
    
    java.util.Arrays.sort(x);
    this.buff += "Sorted: " + java.util.Arrays.toString(x) + "\n";
    
    int found = java.util.Arrays.binarySearch(x, 20);
    this.buff += "Index of 20: " + found + "\n";
    
    int[] y = java.util.Arrays.copyOf(x, 5);
    this.buff += "Copy extended: " + java.util.Arrays.toString(y);
  }

  public String toString() {
    return this.buff;
  }
}
```

### ArraysComparison Class - Comparison Methods

Illustrates array equality comparison and bulk modification using Arrays utility methods.

```java
class ArraysComparison {
  private String buff = null;

  public ArraysComparison() {
    int[] x = {1, 2, 3};
    int[] y = {1, 2, 3};
    int[] z = {3, 2, 1};
    
    this.buff = "x equals y: " + java.util.Arrays.equals(x, y) + "\n";
    this.buff += "x equals z: " + java.util.Arrays.equals(x, z) + "\n";
    
    java.util.Arrays.fill(z, 0);
    this.buff += "z after fill: " + java.util.Arrays.toString(z);
  }

  public String toString() {
    return this.buff;
  }
}
```

### Expected Arrays Output

```
[A, B, C]
[A, B, C]
[10, 20, 30]
[10, 20, 30]
[1.1, 2.2, 3.3]
[1.1, 2.2, 3.3]
Original: [30, 10, 20]
Sorted: [10, 20, 30]
Index of 20: 1
Copy extended: [10, 20, 30, 0, 0]
x equals y: true
x equals z: false
z after fill: [0, 0, 0]
```

Complete output showing array operations and utility method results.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Wrapper Class Methods

Source file: `examples/java/Example04.java`

This example demonstrates the comprehensive API provided by Java's wrapper classes. It explores various methods available in Character, Integer, and Double wrapper classes including object creation, value extraction, comparison, validation, and transformation operations. The code shows factory methods like valueOf(), primitive conversion methods, equality testing, character type checking, case conversion, and numeric comparisons. Each class focuses on specific wrapper class functionality with practical examples of method usage.

### CharacterCreation Class

Shows Character wrapper object creation and conversion methods for extracting primitive values.

```java
class CharacterCreation {
  private String buff = null;

  public CharacterCreation() {
    Character x = Character.valueOf('A');
    char y = x.charValue();
    String z = x.toString();
    this.buff = String.format("valueOf: %c, charValue: %c, toString: %s",
                              x, y, z);
  }

  public String toString() {
    return this.buff;
  }
}
```

### CharacterComparison Class

Illustrates different methods for comparing Character objects using equality and comparison operations.

```java
class CharacterComparison {
  private String buff = null;

  public CharacterComparison() {
    Character x = 'A';
    Character y = 'A';
    boolean eq = x.equals(y);
    int cmp1 = Character.compare('A', 'A');
    int cmp2 = x.compareTo(y);
    this.buff = String.format("equals: %b, compare: %d, compareTo: %d",
                              eq, cmp1, cmp2);
  }

  public String toString() {
    return this.buff;
  }
}
```

### CharacterValidation Class

Demonstrates character type validation using static utility methods for digit and letter checking.

```java
class CharacterValidation {
  private String buff = null;

  public CharacterValidation() {
    char x = 'A';
    boolean digit = Character.isDigit(x);
    boolean letter = Character.isLetter(x);
    this.buff = String.format("isDigit: %b, isLetter: %b", digit, letter);
  }

  public String toString() {
    return this.buff;
  }
}
```

### CharacterTransformation Class

Shows character case conversion using static transformation methods for upper and lower case.

```java
class CharacterTransformation {
  private String buff = null;

  public CharacterTransformation() {
    char x = 'A';
    char lower = Character.toLowerCase(x);
    char upper = Character.toUpperCase(x);
    this.buff = String.format("toLowerCase: %c, toUpperCase: %c", lower, upper);
  }

  public String toString() {
    return this.buff;
  }
}
```

### IntegerCreation Class

Illustrates Integer wrapper instantiation and conversion back to primitive int values.

```java
class IntegerCreation {
  private String buff = null;

  public IntegerCreation() {
    Integer x = Integer.valueOf(42);
    int y = x.intValue();
    String z = x.toString();
    this.buff = String.format("valueOf: %d, intValue: %d, toString: %s",
                              x, y, z);
  }

  public String toString() {
    return this.buff;
  }
}
```

### DoubleCreation Class

Shows Double wrapper object creation and extraction of primitive double values with formatting.

```java
class DoubleCreation {
  private String buff = null;

  public DoubleCreation() {
    Double x = Double.valueOf(3.14);
    double y = x.doubleValue();
    String z = x.toString();
    this.buff = String.format("valueOf: %.2f, doubleValue: %.2f, toString: %s",
                              x, y, z);
  }

  public String toString() {
    return this.buff;
  }
}
```

### Expected Wrapper Methods Output

```
valueOf: A, charValue: A, toString: A
equals: true, compare: 0, compareTo: 0
isDigit: false, isLetter: true
toLowerCase: a, toUpperCase: A
valueOf: 42, intValue: 42, toString: 42
equals: true, compare: 0, compareTo: 0
valueOf: 3.14, doubleValue: 3.14, toString: 3.14
equals: true, compare: 0, compareTo: 0
```

Complete output showing wrapper class method functionality.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Code Blocks

Source file: `examples/java/Example05.java`

This example explores different types of code blocks in Java and their execution order. It demonstrates static blocks that execute once when a class is loaded, instance blocks that run before each constructor call, and local blocks that create new variable scopes within methods. The code illustrates the initialization sequence in Java: static blocks first, then instance blocks, followed by constructors, and shows how local blocks can be used for variable scope management and temporary calculations.

### ClassBlock Class - Static Block

Demonstrates static initialization block that executes once during class loading.

```java
class ClassBlock {
  private String buff = null;

  static {
    System.out.printf("Static block executed\n");
  }

  public ClassBlock() {
    this.buff = "Static block executed";
  }

  public String toString() {
    return this.buff;
  }
}
```

### InstanceBlock Class - Instance Block

Shows instance initialization block that runs before constructor during object creation.

```java
class InstanceBlock {
  private String buff = null;

  {
    this.buff = "Instance block executed";
  }

  public InstanceBlock() {
    // Constructor called after instance block
  }

  public String toString() {
    return this.buff;
  }
}
```

### ConstructorBlock Class - Execution Order

Illustrates the sequence of execution between instance initialization block and constructor.

```java
class ConstructorBlock {
  private String buff = null;

  {
    this.buff = "Instance block executed\n";
  }

  public ConstructorBlock() {
    this.buff += "Constructor called";
  }

  public String toString() {
    return this.buff;
  }
}
```

### MethodBlock Class - Local Blocks

Demonstrates local code block within a method that creates its own variable scope.

```java
class MethodBlock {
  private String buff = null;

  public MethodBlock() {
    this.executeMethodBlock();
  }

  private void executeMethodBlock() {
    {
      String temp = "Method block executed";
      this.buff = temp;
    }
  }

  public String toString() {
    return this.buff;
  }
}
```

### LocalBlock Class - Variable Scope

Shows variable accessibility between outer method scope and inner local block scope.

```java
class LocalBlock {
  private String buff = null;

  public LocalBlock() {
    String outer = "Local";
    {
      String inner = " block executed";
      this.buff = outer + inner;
    }
  }

  public String toString() {
    return this.buff;
  }
}
```

### Expected Code Blocks Output

```
Static block executed
Static block executed
Instance block executed
Instance block executed
Constructor called
Method block executed
Local block executed
```

Shows execution order: static → instance → constructor → method blocks.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Exception Handling

Source file: `examples/java/Example06.java`

This example demonstrates essential Java exception handling patterns. It covers multiple catch blocks with exception hierarchy, method throws declarations, and custom exception creation. The code focuses on practical exception handling techniques commonly used in Java applications.

### BasicException Class - Multiple Catch Blocks

Shows exception hierarchy handling with specific exceptions caught before general ones.

```java
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
```

### ThrowsException Class - Method Throws Declaration

Shows method signature with throws clause and explicit exception throwing using throw statement.

```java
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
```

### CustomException Class - User-Defined Exception

Demonstrates creating custom exception classes by extending Exception for application-specific errors.

```java
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
```

### Expected Exception Handling Output

```
Null pointer caught: NullPointerException
Method threw exception: Invalid argument provided
Custom exception: Input cannot be empty
```

Shows various exception handling scenarios and their corresponding output messages.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Classes and Objects

Source file: `examples/java/Example07.java`

This example demonstrates essential Java class and object concepts. It covers basic class creation, constructors and field initialization, object interaction through methods, and access modifiers. The code focuses on practical object-oriented programming techniques commonly used in Java applications.

### BasicClass - Defining a Class and Creating Objects

Shows how to define a simple class with fields and methods, and instantiate objects from it.

```java
class BasicClass {
  private String name;

  public BasicClass(String name) {
    this.name = name;
  }

  public String greet() {
    return "Hello, " + this.name + "!";
  }
}
```

### ConstructorOverload - Multiple Constructors

Demonstrates constructor overloading with different parameter lists for flexible object creation.

```java
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
```

### ObjectInteraction - Calling Methods on Objects

Demonstrates how one class can create and interact with another class’s objects using method calls.

```java
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
```

### AccessModifiers - Controlling Visibility

Shows how `public`, `private`, and `protected` control access to fields and methods in a class.

```java
class AccessModifiers {
  public int publicValue = 1;        // visible everywhere
  protected int protectedValue = 2;  // visible in same package and subclasses
  private int privateValue = 3;      // visible only in this class

  public int getPrivateValue() {
    return this.privateValue;
  }
}
```

### Expected Classes and Objects Output

```
Hello, Alice!
Coordinates: (0, 0)
Coordinates: (3, 0)
Coordinates: (3, 4)
Sum calculated: 12
Access public: 1
Access protected: 2
Access private (via getter): 3
```

Shows how classes are defined, instantiated, and used to encapsulate data and behavior in Java, with access modifiers controlling visibility.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Inheritance

Source file: `examples/java/Example08.java`

This example demonstrates Java inheritance. It covers class extension, field reuse, constructor chaining with `super`, and method overriding. The code focuses on basic inheritance patterns without polymorphism.

### Animal and Dog Classes - Extending a Base Class

Shows how a subclass inherits fields and methods from a parent class.

```java
class Animal {
  protected String name;

  public Animal(String name) {
    this.name = name;
  }

  public String info() {
    return "Animal name: " + this.name;
  }
}

class Dog extends Animal {
  public Dog(String name) {
    super(name); // call parent constructor
  }

  public String bark() {
    return this.name + " barks loudly";
  }
}
```

### MethodOverride Class - Overriding Parent Method

Demonstrates how a subclass can redefine a method from its parent class.

```java
class Cat extends Animal {
  public Cat(String name) {
    super(name);
  }

  @Override
  public String info() {
    return "Cat name: " + this.name;
  }
}
```

### Expected Inheritance Output

```
Animal name: Rex
Rex barks loudly
Cat name: Milo
```

Shows basic inheritance and method overriding in Java.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Static Members

Source file: `examples/java/Example11.java`

This example demonstrates the use of static fields, methods, and blocks in Java. It focuses on shared data, utility methods, and class-level initialization.

### Static Fields - Shared Across All Instances

Shows how a static field is shared among all objects of a class.

```java
class Counter {
  public static int count = 0;

  public Counter() {
    count++;
  }
}
```

### Static Methods - Utility Functions

Demonstrates methods that can be called without creating an instance of the class.

```java
class MathHelper {
  public static int square(int x) {
    return x * x;
  }
}
```

### Static Blocks - Class Initialization

Shows a static block that runs once when the class is loaded.

```java
class Config {
  public static String VERSION;

  static {
    VERSION = "1.0.0";
    System.out.println("Config loaded: version " + VERSION);
  }
}
```

### Using Static Members

```java
public class StaticExample {
  public static void main(String[] args) {
    System.out.println("Initial count: " + Counter.count);
    new Counter();
    new Counter();
    System.out.println("Count after creating 2 objects: " + Counter.count);

    int sq = MathHelper.square(5);
    System.out.println("Square of 5 is " + sq);

    // Trigger static block
    String v = Config.VERSION;
  }
}
```

### Expected Static Members Output

```
Initial count: 0
Count after creating 2 objects: 2
Square of 5 is 25
Config loaded: version 1.0.0
```

Shows how static fields, methods, and blocks are used for class-level data and behavior in Java.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Interfaces

Source file: `examples/java/Example09.java`

This example demonstrates Java interfaces. It covers interface definition, implementation in classes, and multiple interface usage. The code focuses on practical interface patterns.

### Greeter Interface - Defining and Implementing

Shows how to define an interface and implement it in a class.

```java
interface Greeter {
  String greet(String name);
}

class EnglishGreeter implements Greeter {
  public String greet(String name) {
    return "Hello, " + name + "!";
  }
}
```

### Multiple Interfaces - Implementing More Than One

Demonstrates a class implementing multiple interfaces.

```java
interface Logger {
  void log(String message);
}

class ConsoleGreeter implements Greeter, Logger {
  public String greet(String name) {
    return "Hi, " + name;
  }

  public void log(String message) {
    System.out.println("LOG: " + message);
  }
}
```

### Expected Interfaces Output

```
Hello, Alice!
Hi, Bob
LOG: Greeting sent to Bob
```

Shows how interfaces define contracts and how classes can implement one or more interfaces in Java.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Packages and Imports

Source file: `examples/java/Example10.java`

This example demonstrates how to organize Java classes into packages and how to use `import` statements to access classes from other packages. The code focuses on practical package structuring and class reuse.

### Defining a Package

Shows how to declare a package at the top of a Java source file.

```java
// File: myapp/util/MathUtils.java
package myapp.util;

public class MathUtils {
  public static int square(int x) {
    return x * x;
  }
}
```

### Importing Classes from Packages

Demonstrates how to import a class from another package to use it in your code.

```java
// File: myapp/MainApp.java
package myapp;

import myapp.util.MathUtils;

public class MainApp {
  public static void main(String[] args) {
    int num = 5;
    int sq = MathUtils.square(num);
    System.out.println("Square of " + num + " is " + sq);
  }
}
```

### Using Fully Qualified Class Names

Shows an alternative to `import` by using the full package path.

```java
public class FullyQualifiedExample {
  public static void main(String[] args) {
    int num = 3;
    int sq = myapp.util.MathUtils.square(num);
    System.out.println("Square of " + num + " is " + sq);
  }
}
```

### Expected Packages and Imports Output

```
Square of 5 is 25
Square of 3 is 9
```

Shows how to organize classes into packages, import them for reuse, and use fully qualified names in Java.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Enums

Source file: `examples/java/Example12.java`

This example demonstrates Java enumerations (enums). It covers defining enums, adding fields and methods, and using enums in classes. The code focuses on practical use of enums for fixed sets of constants.

### Defining a Simple Enum

Shows how to declare an enum with a set of constant values.

```java
enum Day {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

### Using Enums in a Class

Demonstrates using an enum as a field and in methods.

```java
class Schedule {
  private Day day;

  public Schedule(Day day) {
    this.day = day;
  }

  public String getMessage() {
    return "Today is " + this.day;
  }
}
```

### Enum with Fields and Methods

Shows how to associate additional data and behavior with enum values.

```java
enum Status {
  NEW(1), IN_PROGRESS(2), DONE(3);

  private int code;

  Status(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
```

### Using Enums

```java
public class EnumExample {
  public static void main(String[] args) {
    Schedule sched = new Schedule(Day.MONDAY);
    System.out.println(sched.getMessage());

    Status s = Status.IN_PROGRESS;
    System.out.println("Status: " + s + ", code: " + s.getCode());
  }
}
```

### Expected Enums Output

```
Today is MONDAY
Status: IN_PROGRESS, code: 2
```

Shows how enums define a fixed set of constants, can carry data and methods, and are used in Java classes.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Collections: List

Source file: `examples/java/Example13.java`

This example demonstrates basic usage of the `List` interface in Java. It covers creating a list, adding and retrieving elements, and iterating over the list. The code focuses on essential patterns commonly used in Java applications.

### Creating and Populating a List

Shows how to create an `ArrayList` and add elements to it.

```java
import java.util.ArrayList;
import java.util.List;

class ListExample {
  public static void main(String[] args) {
    List<String> fruits = new ArrayList<>();
    fruits.add("Apple");
    fruits.add("Banana");
    fruits.add("Cherry");

    System.out.println("First fruit: " + fruits.get(0));
  }
}
```

### Iterating Over a List

Demonstrates iterating through elements using a for-each loop.

```java
for (String fruit : fruits) {
  System.out.println(fruit);
}
```

### Common List Operations

Shows basic list operations like removing and checking elements.

```java
fruits.remove("Banana");
if (fruits.contains("Cherry")) {
  System.out.println("Cherry is in the list");
}
```

### Expected List Output

```
First fruit: Apple
Apple
Banana
Cherry
Cherry is in the list
```

Shows how to create, populate, access, and manipulate a list in Java, demonstrating essential usage of the `List` interface.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Collections: Map

Source file: `examples/java/Example14.java`

This example demonstrates basic usage of the `Map` interface in Java. It covers creating a map, adding and retrieving key-value pairs, and iterating over entries. The code focuses on essential patterns commonly used in Java applications.

### Creating and Populating a Map

Shows how to create a `HashMap` and store key-value pairs.

```java
import java.util.HashMap;
import java.util.Map;

class MapExample {
  public static void main(String[] args) {
    Map<String, Integer> scores = new HashMap<>();
    scores.put("Alice", 85);
    scores.put("Bob", 92);
    scores.put("Charlie", 78);

    System.out.println("Score of Alice: " + scores.get("Alice"));
  }
}
```

### Iterating Over Map Entries

Demonstrates how to iterate over keys and values in a map.

```java
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
  System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

### Checking for Keys and Values

Shows common utility methods of a map.

```java
if (scores.containsKey("Bob")) {
  System.out.println("Bob is in the map with score " + scores.get("Bob"));
}

if (scores.containsValue(78)) {
  System.out.println("Someone scored 78");
}
```

### Expected Map Output

```
Score of Alice: 85
Alice -> 85
Bob -> 92
Charlie -> 78
Bob is in the map with score 92
Someone scored 78
```

Shows how to create, populate, access, and iterate over a map in Java, demonstrating essential usage of the `Map` interface.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Annotations: Basic Usage

Source file: `examples/java/Example15.java`

This example demonstrates basic usage of Java annotations. It covers `@Override` for method overriding and `@Deprecated` to mark deprecated elements. The code focuses on practical, commonly used annotations in Java applications.

### @Override - Indicating Method Overriding

Shows how to use `@Override` to ensure a subclass correctly overrides a method from its parent class.

```java
class Animal {
  public String speak() {
    return "Some generic sound";
  }
}

class Dog extends Animal {
  @Override
  public String speak() {
    return "Woof!";
  }
}
```

### @Deprecated - Marking Deprecated Methods

Demonstrates marking methods as deprecated to indicate they should not be used.

```java
class OldLibrary {
  @Deprecated
  public void oldMethod() {
    System.out.println("This method is outdated");
  }

  public void newMethod() {
    System.out.println("Use this method instead");
  }
}
```

### Using Annotations

```java
public class AnnotationExample {
  public static void main(String[] args) {
    Dog dog = new Dog();
    System.out.println(dog.speak());

    OldLibrary lib = new OldLibrary();
    lib.oldMethod();  // Compiler warning: method is deprecated
    lib.newMethod();
  }
}
```

### Expected Annotations Output

```
Woof!
This method is outdated
Use this method instead
```

Shows how to use `@Override` to validate method overriding and `@Deprecated` to indicate outdated methods in Java.  

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Records

Source file: `examples/java/Example16.java`

This example demonstrates Java Records, a feature introduced in Java 14 that provides a concise way to create immutable data classes. Records automatically generate constructor, getters, equals(), hashCode(), and toString() methods, reducing boilerplate code significantly. The code shows different ways to create and use records, including records with validation, methods, and static factory methods.

### Simple Record Declaration

Shows the basic syntax for declaring a record with automatic method generation.

```java
public record Person(String name, int age) {}
```

Defines a record with two fields. The compiler automatically generates:
- Constructor: `Person(String name, int age)`
- Getters: `name()` and `age()` 
- `equals()`, `hashCode()`, `toString()` methods

### Record with Validation

Demonstrates how to add validation in record constructors using compact constructor syntax.

```java
public record Point(int x, int y) {
  public Point {
    if (x < 0 || y < 0) {
      throw new IllegalArgumentException("Coordinates must be non-negative");
    }
  }
}
```

### Record with Methods

Shows how to add instance and static methods to records while maintaining immutability.

```java
public record Rectangle(double width, double height) {
  public double area() {
    return width * height;
  }
  
  public static Rectangle square(double side) {
    return new Rectangle(side, side);
  }
}
```

### Record vs Traditional Class

Compares a traditional class implementation with its equivalent record version.

```java
// Traditional class (verbose)
public class TodoTraditional {
  private final Long id;
  private final String title;
  private final boolean completed;
  
  public TodoTraditional(Long id, String title, boolean completed) {
    this.id = id;
    this.title = title;
    this.completed = completed;
  }
  
  public Long getId() { return id; }
  public String getTitle() { return title; }
  public boolean isCompleted() { return completed; }
  
  // equals, hashCode, toString methods...
}

// Record equivalent (concise)
public record Todo(Long id, String title, boolean completed) {}
```

### Records with Collections

Demonstrates using records to model data structures with collections and nested records.

```java
public record Book(String title, String author, List<String> genres) {
  public Book {
    genres = List.copyOf(genres); // Defensive copy for immutability
  }
  
  public boolean hasGenre(String genre) {
    return genres.contains(genre);
  }
}

public record Library(String name, List<Book> books) {
  public long bookCount() {
    return books.size();
  }
  
  public List<Book> booksByAuthor(String author) {
    return books.stream()
               .filter(book -> book.author().equals(author))
               .toList();
  }
}
```

### Expected Records Output

```
Person: Person[name=Alice, age=30]
Point: Point[x=10, y=20]
Rectangle area: 200.0
Square area: 25.0
Traditional vs Record equality: true
Book genres: [Fiction, Mystery]
Library has 3 books
Books by Jane Doe: [Book[title=Mystery Novel, author=Jane Doe, genres=[Mystery, Thriller]]]
```

Shows various record operations including automatic toString(), method calls, and collection handling.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Memory Management

Source file: `examples/java/Example17.java`

This example demonstrates Java memory management concepts including stack vs heap allocation, object references, garbage collection behavior, and memory optimization techniques. The code shows how primitive variables, objects, method calls, and different data structures affect memory usage and performance. Understanding these concepts is crucial for writing efficient Java applications and avoiding memory leaks.

### Stack vs Heap Allocation

Shows the difference between stack-allocated primitives and heap-allocated objects.

```java
public class MemoryDemo {
    public void stackVsHeap() {
        // Stack allocation - primitives
        int primitive = 42;           // Stored directly on stack
        boolean flag = true;          // Stored directly on stack
        char letter = 'A';            // Stored directly on stack
        
        // Heap allocation - objects
        String text = "Hello";        // Reference on stack, object on heap
        Integer wrapper = 100;        // Reference on stack, object on heap
        int[] array = {1, 2, 3};     // Reference on stack, array on heap
    }
}
```

### Object References and Memory

Demonstrates how object references work and the difference between reference equality and object equality.

```java
public class ReferenceDemo {
    public void referenceVsValue() {
        // Same object reference
        String str1 = "Hello";
        String str2 = "Hello";        // String pool - same reference
        
        // Different object references
        String str3 = new String("Hello");  // Force new object on heap
        String str4 = new String("Hello");  // Another new object on heap
        
        // Reference comparison vs content comparison
        boolean sameRef1 = (str1 == str2);     // true - same reference
        boolean sameRef2 = (str3 == str4);     // false - different references
        boolean sameContent = str3.equals(str4); // true - same content
    }
}
```

### Method Call Stack

Shows how method calls create stack frames and how local variables are managed.

```java
public class StackFrameDemo {
    public void methodA() {
        int localVar = 10;            // Stack frame for methodA
        String localObj = "A";        // Reference in stack, object in heap
        methodB(localVar);            // New stack frame created
    }
    
    public void methodB(int param) {
        int anotherVar = param * 2;   // Stack frame for methodB
        String anotherObj = "B";      // New reference and object
        methodC();                    // Another stack frame
    }
    
    public void methodC() {
        // Deepest stack frame
        int deepVar = 100;
    }
    // When methods return, stack frames are popped
}
```

### Garbage Collection Behavior

Demonstrates object lifecycle and when objects become eligible for garbage collection.

```java
public class GarbageCollectionDemo {
    public void gcExample() {
        // Object creation
        StringBuilder sb1 = new StringBuilder("Initial");
        StringBuilder sb2 = new StringBuilder("Second");
        
        // sb1 is eligible for GC after this point
        sb1 = null;
        
        // Reassignment makes original sb2 eligible for GC
        sb2 = new StringBuilder("Third");
        
        // Local method scope - all local references become
        // eligible for GC when method ends
    }
    
    public String createAndReturn() {
        String temp = "Temporary";    // Local reference
        return temp;                  // Object survives method return
    }
}
```

### Memory Optimization Techniques

Shows techniques for efficient memory usage including object pooling and avoiding unnecessary allocations.

```java
public class MemoryOptimization {
    // String concatenation - inefficient
    public String inefficientConcat(String[] words) {
        String result = "";
        for (String word : words) {
            result += word + " ";     // Creates new String objects each time
        }
        return result;
    }
    
    // String concatenation - efficient
    public String efficientConcat(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word).append(" "); // Reuses same buffer
        }
        return sb.toString();
    }
    
    // Object reuse vs recreation
    public void objectReuse() {
        // Inefficient - creates new objects
        for (int i = 0; i < 1000; i++) {
            List<String> list = new ArrayList<>(); // New object each iteration
            // ... use list
        }
        
        // More efficient - reuse object
        List<String> reusableList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            reusableList.clear();      // Reuse same object
            // ... use list
        }
    }
}
```

### Memory Leaks Prevention

Demonstrates common memory leak scenarios and how to prevent them.

```java
public class MemoryLeakDemo {
    private List<String> cache = new ArrayList<>();
    
    // Memory leak - cache grows indefinitely
    public void leakyCache(String data) {
        cache.add(data);              // Never removed
    }
    
    // Fixed - with size limit
    public void boundedCache(String data) {
        if (cache.size() > 1000) {
            cache.remove(0);          // Remove oldest entry
        }
        cache.add(data);
    }
    
    // Inner class memory leak
    public class InnerClass {
        // Holds implicit reference to outer class
        private String data;
        
        public InnerClass(String data) {
            this.data = data;
        }
    }
    
    // Fixed with static inner class
    public static class StaticInnerClass {
        // No implicit reference to outer class
        private String data;
        
        public StaticInnerClass(String data) {
            this.data = data;
        }
    }
}
```

### Expected Memory Management Output

```
Stack primitive: 42
Heap object: Hello World
Reference equality (string pool): true
Reference equality (new objects): false
Content equality: true
Method call depth: 3
StringBuilder efficient: Hello World Java Programming
ArrayList reuse demonstration completed
Cache size after cleanup: 1000
Static inner class created without outer reference
Memory demonstration completed
```

Shows various aspects of Java memory management including stack/heap allocation, reference handling, and optimization techniques.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Event Listeners

Source file: `examples/java/Example18.java`

This example demonstrates the event listener pattern in Java, also known as the
Observer pattern. Event listeners provide a way to implement decoupled
communication between objects where one object (publisher) notifies multiple
other objects (listeners) about events without knowing the specific details of
those listeners. This pattern is fundamental in Java applications, particularly
in GUI frameworks and web applications where components need to respond to user
actions or system events.

### Event Class Definition

Base class for all events that extends Java's built-in event infrastructure concepts.

```java
class AppEvent {
    private String eventType;
    private long timestamp;
    
    public AppEvent(String eventType) {
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getEventType() { return eventType; }
    public long getTimestamp() { return timestamp; }
}
```

Defines an event with a type identifier and automatic timestamp. Events carry information about what happened and when.

### Event Listener Interface

Contract that all event listeners must implement to receive notifications.

```java
interface AppEventListener {
    void onEvent(AppEvent event);
}
```

Simple interface with a single method that gets called when an event occurs. This follows the single responsibility principle.

### Concrete Listener Implementation

Example listener that processes events by logging them with a custom name identifier.

```java
class LoggingListener implements AppEventListener {
    private String name;
    
    public LoggingListener(String name) {
        this.name = name;
    }
    
    @Override
    public void onEvent(AppEvent event) {
        System.out.printf("[%s] Event: %s at %d%n", 
            name, event.getEventType(), event.getTimestamp());
    }
}
```

Concrete implementation that identifies itself with a name and formats event information for output.

### Event Publisher Pattern

Central class that manages listeners and publishes events to all registered observers.

```java
class EventPublisher {
    private List<AppEventListener> listeners = new ArrayList<>();
    
    public void addListener(AppEventListener listener) {
        listeners.add(listener);
    }
    
    public void publishEvent(String eventType) {
        AppEvent event = new AppEvent(eventType);
        for (AppEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
```

### Publisher Registration and Notification

The publisher maintains a list of listeners and notifies them all when events occur.

```java
// Create event publisher
EventPublisher publisher = new EventPublisher();

// Register multiple listeners
publisher.addListener(new LoggingListener("Logger1"));
publisher.addListener(new LoggingListener("Logger2"));

// Publish events to all registered listeners
publisher.publishEvent("APPLICATION_STARTED");
publisher.publishEvent("USER_LOGIN");
```

### Event Flow Architecture

The event listener pattern follows this execution flow:

1. **Registration**: Listeners register themselves with the publisher
2. **Event Trigger**: Something happens that needs to notify listeners
3. **Event Creation**: Publisher creates an event object with relevant data
4. **Notification**: Publisher calls the listener method on all registered listeners
5. **Processing**: Each listener processes the event according to its logic

### Benefits and Use Cases

**Decoupling**: Publishers don't need to know about specific listeners, only the interface contract.

**Extensibility**: New listeners can be added without modifying existing code.

**Multiple Responses**: One event can trigger multiple different actions simultaneously.

**Common Applications**:
- GUI event handling (button clicks, window events)
- Web application lifecycle events (startup, shutdown)
- Database change notifications
- File system monitoring
- Custom business logic triggers

### Expected Event Listeners Output

```
=== Simple Event Listener Example ===

[Logger1] Event: APPLICATION_STARTED at 1699123456789
[Logger2] Event: APPLICATION_STARTED at 1699123456789
[Logger1] Event: USER_LOGIN at 1699123456790
[Logger2] Event: USER_LOGIN at 1699123456790
[Logger1] Event: DATA_PROCESSED at 1699123456791
[Logger2] Event: DATA_PROCESSED at 1699123456791
[Logger1] Event: APPLICATION_STOPPED at 1699123456792
[Logger2] Event: APPLICATION_STOPPED at 1699123456792
```

Shows how multiple listeners receive and process the same events independently.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Lambda Expressions

Source file: `examples/java/Example19.java`

This example demonstrates lambda expressions in Java, a feature introduced in Java 8 that enables functional programming concepts. Lambda expressions provide a concise way to represent anonymous functions and are particularly useful for implementing functional interfaces, working with collections, and creating cleaner, more readable code. They eliminate the verbosity of anonymous inner classes while maintaining the same functionality and type safety.

### Lambda Syntax and Basic Usage

Lambda expressions use the arrow operator (->) to separate parameters from the body.

```java
// Basic lambda syntax: (parameters) -> expression
Runnable simpleTask = () -> System.out.println("Simple lambda task executed");

// Lambda with parameters
MathOperation addition = (a, b) -> a + b;
MathOperation multiplication = (a, b) -> a * b;
```

The syntax consists of three parts: parameter list in parentheses, arrow operator, and the body which can be an expression or statement block.

### Traditional vs Lambda Approach

Comparison between anonymous inner classes and lambda expressions for cleaner code.

```java
// Traditional anonymous inner class approach
Thread traditionalWorker = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Traditional worker started");
        System.out.println("Traditional worker finished");
    }
});

// Lambda expression approach - much more concise
Thread lambdaWorker = new Thread(() -> {
    for (int i = 1; i <= 5; i++) {
        System.out.println("Lambda worker dice: passo " + i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
    System.out.println("Lambda worker terminato");
});
```

Lambda expressions significantly reduce boilerplate code while maintaining the same functionality and readability.

### Functional Interface Requirements

Lambda expressions can only be used with functional interfaces (interfaces with exactly one abstract method).

```java
@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}

@FunctionalInterface
interface StringProcessor {
    String process(String input);
}
```

The `@FunctionalInterface` annotation ensures compile-time verification that the interface has exactly one abstract method.

### Lambda with Multiple Statements

When lambda body contains multiple statements, use curly braces and explicit return statements.

```java
StringProcessor processor = (str) -> {
    String result = str.toUpperCase();
    result = result.trim();
    return result + "!";
};

System.out.println("Processed: " + processor.process("  hello world  "));
```

Multi-statement lambdas require explicit return statements and proper block syntax with curly braces.

### Thread Creation with Lambda

Lambda expressions excel in thread creation, making concurrent programming more readable.

```java
Thread worker = new Thread(() -> {
    for (int i = 1; i <= 5; i++) {
        System.out.println("Worker dice: passo " + i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    System.out.println("Worker terminato");
});

worker.start();
worker.join();
```

### Method References and Higher-Order Functions

Lambda expressions enable functional programming patterns with method passing as parameters.

```java
private static int calculate(int a, int b, MathOperation operation) {
    return operation.operate(a, b);
}

// Usage with lambda expressions
int sum = calculate(5, 3, (a, b) -> a + b);
int product = calculate(5, 3, (a, b) -> a * b);
```

This pattern allows methods to accept behavior as parameters, enabling flexible and reusable code design.

### Expected Lambda Expressions Output

```
=== Lambda Expressions with Threading ===

Traditional worker started
Traditional worker finished

Lambda worker dice: passo 1
Main continua a girare...
Lambda worker dice: passo 2
Lambda worker dice: passo 3
Lambda worker dice: passo 4
Lambda worker dice: passo 5
Lambda worker terminato

Main terminato

=== Lambda Expressions Basics ===

Simple lambda task executed
Addition: 5 + 3 = 8
Multiplication: 5 * 3 = 15
Processed: HELLO WORLD!
```

Shows lambda expressions in action with threading, mathematical operations, and string processing examples.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Application Lifecycle Threading

Source file: `examples/java/Example20.java`

This example demonstrates advanced threading patterns integrated with application lifecycle management using lambda expressions. It builds upon the previous lambda concepts to show how background threads can be properly managed throughout an application's lifecycle, from startup to graceful shutdown. This pattern is essential for building robust applications that run long-running background tasks, such as data processing, health monitoring, or cleanup operations, while ensuring proper resource management and clean termination.

### Background Service with Lambda Threading

A service class that encapsulates background thread management with lambda-based thread creation.

```java
class BackgroundService {
    private Thread workerThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    public void start() {
        if (running.compareAndSet(false, true)) {
            workerThread = new Thread(() -> {
                while (running.get()) {
                    try {
                        System.out.println("Worker attivo...");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            workerThread.start();
        }
    }
}
```

Uses AtomicBoolean for thread-safe state management and lambda expressions for clean thread creation syntax.

### Graceful Thread Termination

Proper shutdown mechanism that allows threads to complete their work gracefully before termination.

```java
public void stop() {
    if (running.compareAndSet(true, false)) {
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
            
            try {
                workerThread.join(5000); // 5 second timeout
                if (workerThread.isAlive()) {
                    System.out.println("Warning: worker did not stop gracefully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

The shutdown process uses interrupt() to signal termination and join() with timeout to wait for graceful completion.

### Application Context Management

Centralized management of multiple background services with coordinated lifecycle control.

```java
class ApplicationContext {
    private final BackgroundService[] services;
    
    public void start() {
        for (BackgroundService service : services) {
            service.start();
        }
    }
    
    public void stop() {
        for (BackgroundService service : services) {
            service.stop();
        }
    }
}
```

The application context manages multiple services as a cohesive unit, ensuring all background tasks start and stop together.

### Lifecycle Event Integration

Integration with application lifecycle events for automatic background thread management.

```java
class BackgroundThreadListener implements ApplicationLifecycleListener {
    private ApplicationContext applicationContext;
    
    @Override
    public void onApplicationStart() {
        applicationContext = new ApplicationContext();
        applicationContext.start();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (applicationContext != null) {
                applicationContext.stop();
            }
        }));
    }
    
    @Override
    public void onApplicationStop() {
        if (applicationContext != null) {
            applicationContext.stop();
        }
    }
}
```

### Shutdown Hook Integration

JVM shutdown hook ensures background threads terminate cleanly even during unexpected application shutdown.

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    System.out.println("Shutdown hook triggered");
    if (applicationContext != null) {
        applicationContext.stop();
    }
}));
```

Shutdown hooks provide a safety net for resource cleanup when the application terminates unexpectedly.

### Thread Safety with AtomicBoolean

Thread-safe state management using atomic operations instead of synchronized blocks.

```java
private final AtomicBoolean running = new AtomicBoolean(false);

// Thread-safe state changes
if (running.compareAndSet(false, true)) {
    // Start logic - only executes if previous state was false
}

if (running.compareAndSet(true, false)) {
    // Stop logic - only executes if previous state was true
}
```

AtomicBoolean provides lock-free thread safety for simple boolean state management.

### Expected Application Lifecycle Threading Output

```
=== Application Lifecycle Threading Example ===

Application lifecycle event: START
=== Application Context Starting ===
DataProcessor worker started
HealthChecker worker started
LogCleaner worker started
Application context started with 3 background services

Application running... (will stop after 15 seconds)

DataProcessor worker attivo... [DataProcessor-Worker]
HealthChecker worker attivo... [HealthChecker-Worker]
LogCleaner worker attivo... [LogCleaner-Worker]
DataProcessor worker attivo... [DataProcessor-Worker]
HealthChecker worker attivo... [HealthChecker-Worker]
LogCleaner worker attivo... [LogCleaner-Worker]

Application lifecycle event: STOP
=== Application Context Stopping ===
DataProcessor worker interrupted
DataProcessor worker terminato
HealthChecker worker interrupted
HealthChecker worker terminato
LogCleaner worker interrupted
LogCleaner worker terminato
Application context stopped

Main application terminated
```

Shows complete lifecycle management with multiple background services starting, running, and terminating gracefully in response to application events.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## File I/O and Resource Reading

Source file: `examples/java/Example21.java`

This example demonstrates essential file input/output operations in Java, focusing on reading files from classpath resources and text processing. It covers the fundamental patterns needed for reading configuration files, SQL migration files, and other resources packaged with applications.

### Reading from Classpath Resources

Access files packaged within your application JAR using the ClassLoader resource mechanism.

```java
InputStream inputStream = MyClass.class.getClassLoader().getResourceAsStream("path/to/file.txt");
```

The getResourceAsStream() method returns an InputStream for files located in the classpath, typically in src/main/resources.

### BufferedReader for Text Files

Efficient line-by-line reading using BufferedReader with automatic resource management.

```java
try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
    StringBuilder content = new StringBuilder();
    String line;
    
    while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
    }
    
    return content.toString();
}
```

The try-with-resources statement ensures automatic closure of BufferedReader and InputStream resources.

### Text Processing and Filtering

Filter and process text content line by line, useful for SQL files and configuration processing.

```java
for (String line : lines) {
    String trimmedLine = line.trim();
    // Skip comments and empty lines
    if (!trimmedLine.startsWith("--") && !trimmedLine.isEmpty()) {
        content.append(trimmedLine).append("\n");
    }
}
```

Common pattern for removing comments and empty lines from configuration or SQL files.

### Exception Handling for I/O

Proper error handling for I/O operations using IOException and informative error messages.

```java
public static String readResourceSafely(String resourcePath) throws IOException {
    InputStream inputStream = MyClass.class.getClassLoader().getResourceAsStream(resourcePath);
    
    if (inputStream == null) {
        throw new IOException("Resource not found: " + resourcePath);
    }
    
    // Process the stream...
}
```

Always check for null InputStream and throw meaningful exceptions for missing resources.

### Resource Management Best Practices

Use try-with-resources for automatic cleanup of I/O resources.

```java
try (InputStream stream = getResourceAsStream(path);
     BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
    // Process the file
    return processContent(reader);
}
// Resources automatically closed here
```

The try-with-resources statement guarantees proper closure even if exceptions occur during processing.

### Common Use Cases

File I/O patterns are essential for:
- Reading SQL migration files from resources
- Loading configuration files packaged with applications
- Processing template files and data files
- Reading property files and initialization data

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Try-with-resources Pattern

Source file: `examples/java/lang/Example22.java`

This example demonstrates the try-with-resources statement for automatic resource management in Java. The try-with-resources pattern ensures that resources implementing AutoCloseable are automatically closed when the try block exits, preventing resource leaks and simplifying exception handling. This pattern is essential for working with files, database connections, network resources, and any custom resources that need cleanup.

### Basic Try-with-resources Syntax

Shows the fundamental syntax with automatic resource closure.

```java
try (BufferedReader reader = Files.newBufferedReader(path)) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} // reader.close() called automatically here
```

The resource is declared in the try statement parentheses and automatically closed when the block exits.

### Multiple Resources Management

Demonstrates managing multiple resources in a single try-with-resources statement.

```java
try (BufferedReader reader = Files.newBufferedReader(sourceFile);
     BufferedWriter writer = Files.newBufferedWriter(targetFile)) {
    
    String line;
    while ((line = reader.readLine()) != null) {
        writer.write(line);
        writer.newLine();
    }
} // Both reader and writer closed automatically (in reverse order)
```

Multiple resources are separated by semicolons and closed in reverse declaration order.

### Custom AutoCloseable Implementation

Shows how to create custom resources that work with try-with-resources.

```java
class DatabaseConnection implements AutoCloseable {
    private String connectionString;
    private boolean isOpen;
    
    public DatabaseConnection(String connectionString) {
        this.connectionString = connectionString;
        this.isOpen = true;
    }
    
    @Override
    public void close() {
        if (isOpen) {
            System.out.println("Database connection closed");
            isOpen = false;
        }
    }
}

// Usage with try-with-resources
try (DatabaseConnection conn = new DatabaseConnection("localhost:5432")) {
    // Use connection
} // conn.close() called automatically
```

Custom resources must implement AutoCloseable or Closeable interface with a close() method.

### Exception Handling with Try-with-resources

Demonstrates suppressed exceptions when both try block and close() method throw exceptions.

```java
try (ProblematicResource resource = new ProblematicResource()) {
    resource.doSomething(); // Throws exception
} catch (Exception e) {
    System.out.println("Main exception: " + e.getMessage());
    
    // Check for suppressed exceptions from close() method
    Throwable[] suppressed = e.getSuppressed();
    for (Throwable t : suppressed) {
        System.out.println("Suppressed: " + t.getMessage());
    }
}
```

If both the try block and close() method throw exceptions, the close() exception is suppressed.

### Database Operations Example

Real-world usage pattern for database operations with proper resource management.

```java
try (Connection conn = dataSource.getConnection();
     CallableStatement stmt = conn.prepareCall("SELECT * FROM sp_get_patient(?)")) {
    
    stmt.setLong(1, patientId);
    
    try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            // Process results
        }
    }
} // All resources closed automatically in reverse order
```

Common pattern for database access ensuring proper cleanup of connections, statements, and result sets.

### Benefits and Best Practices

**Automatic Resource Management**: Resources are guaranteed to be closed, preventing leaks.

**Exception Safety**: Resources are closed even if exceptions occur in the try block.

**Cleaner Code**: Eliminates the need for explicit finally blocks for resource cleanup.

**Multiple Resource Support**: Handle multiple resources with proper cleanup ordering.

**Best Practices**:
- Always use try-with-resources for AutoCloseable resources
- Declare resources in the try statement, not before
- Handle exceptions appropriately, including suppressed exceptions
- Implement AutoCloseable properly in custom resource classes

### Expected Try-with-resources Output

```
Database connection opened to: localhost:5432
Executing query: SELECT * FROM users
Query executed successfully
Database connection closed

File copied successfully
Copied content: Content to copy

Main exception: Something went wrong in doSomething()
Suppressed exception: Error during close()
```

Shows automatic resource management with both successful operations and exception handling scenarios.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## JUnit 5 Testing Framework

Source file: `examples/java/lang/Example23.java`

This example demonstrates JUnit 5 (Jupiter) testing framework for Java applications. JUnit is the most widely used testing framework in Java, providing annotations, assertions, and lifecycle management for unit tests. The framework enables test-driven development, automated testing, and ensures code quality through comprehensive test coverage. JUnit 5 introduces modern features like parameterized tests, nested test classes, and improved assertions.

### Basic Test Structure

Shows the fundamental structure of JUnit 5 tests with annotations and assertions.

```java
class CalculatorTest {
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    @DisplayName("Addition should work correctly")
    void testAddition() {
        assertEquals(8, calculator.add(5, 3), "5 + 3 should equal 8");
        assertEquals(0, calculator.add(-1, 1), "-1 + 1 should equal 0");
    }
}
```

Basic test class with setup method and assertion-based test verification.

### Lifecycle Annotations

Demonstrates JUnit 5 lifecycle management annotations for test setup and cleanup.

```java
@BeforeAll
static void setUpAll() {
    // Run once before all tests in the class
}

@BeforeEach
void setUp() {
    // Run before each test method
}

@AfterEach
void tearDown() {
    // Run after each test method
}

@AfterAll
static void tearDownAll() {
    // Run once after all tests in the class
}
```

Lifecycle annotations provide hooks for test preparation and cleanup at different stages.

### Exception Testing

Shows how to test expected exceptions using assertThrows().

```java
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
```

Exception testing verifies that methods throw expected exceptions under specific conditions.

### Parameterized Tests

Demonstrates testing multiple inputs using parameterized tests with @ValueSource and @CsvSource.

```java
@ParameterizedTest
@DisplayName("Prime number detection should work correctly")
@ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23})
void testPrimeNumbers(int number) {
    assertTrue(calculator.isPrime(number), number + " should be prime");
}

@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "5, 7, 12", 
    "-1, 1, 0"
})
void testAdditionWithCsv(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

Parameterized tests reduce code duplication by running the same test logic with different inputs.

### Nested Test Classes

Shows test organization using nested classes for logical grouping.

```java
@DisplayName("UserService Tests")
class UserServiceTest {
    
    @Nested
    @DisplayName("User Addition Tests")
    class UserAdditionTest {
        
        @Test
        @DisplayName("Should add valid user successfully")
        void testAddValidUser() {
            userService.addUser("john_doe");
            assertEquals(1, userService.getUserCount());
            assertTrue(userService.userExists("john_doe"));
        }
    }
}
```

Nested classes help organize related tests and provide better test structure and readability.

### Assertions and Timeouts

Comprehensive assertion methods for different verification needs.

```java
@Test
void testAssertions() {
    // Basic assertions
    assertEquals(expected, actual, "Values should be equal");
    assertTrue(condition, "Condition should be true");
    assertFalse(condition, "Condition should be false");
    assertNull(value, "Value should be null");
    assertNotNull(value, "Value should not be null");
    
    // Collection assertions
    assertIterableEquals(expectedList, actualList);
    
    // Timeout assertions
    assertTimeout(Duration.ofMillis(100), () -> {
        // Code that should complete within 100ms
    });
}
```

JUnit 5 provides rich assertion methods for comprehensive test verification.

### Test Dependencies and Maven Integration

Maven configuration for JUnit 5 testing with Surefire plugin.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

Proper Maven configuration enables test execution with `mvn test` command.

### Running Tests

Tests can be executed through:
- **IDE**: Right-click test class and select "Run Tests"
- **Maven**: `mvn test` command executes all tests
- **Gradle**: `gradle test` for Gradle-based projects
- **Command Line**: Direct JUnit console launcher

### Expected JUnit Output

```
Calculator Tests:
✓ Addition should work correctly
✓ Division should work correctly  
✓ Division by zero should throw exception
✓ Square root should work for positive numbers
✓ Prime number detection should work correctly [2]
✓ Prime number detection should work correctly [3]
✓ Prime number detection should work correctly [5]
✓ Performance test - calculation should complete quickly

UserService Tests:
  User Addition Tests:
    ✓ Should add valid user successfully
    ✓ Should throw exception for null username
  User Removal Tests:
    ✓ Should remove existing user successfully

Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
```

Shows successful test execution with detailed test results and timing information.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## JSON Processing

Source file: `examples/java/lang/Example24.java`

This example demonstrates JSON processing in Java using the org.json library, which provides the simplest and most straightforward API for JSON operations. The library is lightweight, has no external dependencies, and offers intuitive methods for creating, parsing, and manipulating JSON data structures. It's perfect for REST API development, configuration processing, and data serialization tasks.

### Basic JSON Object Creation

Shows the fundamental operations for creating and accessing JSON objects.

```java
JSONObject patient = new JSONObject();
patient.put("nome", "Mario");
patient.put("cognome", "Rossi");
patient.put("eta", 42);
patient.put("attivo", true);
patient.put("note", JSONObject.NULL); // Explicit null value

// Access values with automatic type conversion
String nome = patient.getString("nome");
int eta = patient.getInt("eta");
boolean attivo = patient.getBoolean("attivo");
```

The JSONObject class provides intuitive put() and get() methods with automatic type conversion.

### JSON Arrays and Nested Objects

Demonstrates complex data structures with arrays and nested JSON objects.

```java
JSONObject address = new JSONObject();
address.put("via", "Via Roma 123");
address.put("citta", "Milano");
address.put("cap", "20100");

JSONArray telefoni = new JSONArray();
telefoni.put("02-1234567");
telefoni.put("347-9876543");

JSONObject contact = new JSONObject();
contact.put("id", 1001L);
contact.put("nome", "Giulia");
contact.put("indirizzo", address);
contact.put("telefoni", telefoni);

// Access nested values
String citta = contact.getJSONObject("indirizzo").getString("citta");
String primoTelefono = contact.getJSONArray("telefoni").getString(0);
```

Nested structures are built naturally using JSONObject and JSONArray composition.

### JSON String Parsing

Shows how to parse JSON received from external sources like APIs or files.

```java
String jsonString = """
  {
    "result_code": 0,
    "message": "Success", 
    "patient": {
      "id": 12345,
      "nome": "Anna",
      "cognome": "Bianchi"
    }
  }
  """;

// Parse JSON string
JSONObject response = new JSONObject(jsonString);

// Extract values
int resultCode = response.getInt("result_code");
String message = response.getString("message");

if (response.has("patient")) {
  JSONObject patient = response.getJSONObject("patient");
  String patientName = patient.getString("nome") + " " + patient.getString("cognome");
}
```

The JSONObject constructor directly parses JSON strings with automatic validation.

### Safe Value Access and Error Handling

Demonstrates defensive programming with JSON operations using optional methods.

```java
// Safe access with default values
String optValue = json.optString("missing_field", "default_value");
int optInt = json.optInt("missing_number", 42);
boolean optBool = json.optBoolean("missing_flag", false);

// Check existence before access
if (json.has("existing_field")) {
  String value = json.getString("existing_field");
}

// Exception handling for invalid JSON
try {
  JSONObject obj = new JSONObject(invalidJsonString);
} catch (JSONException e) {
  System.out.println("Invalid JSON: " + e.getMessage());
}
```

The opt methods provide safe access with default values, while has() checks field existence.

### Converting Java Objects to JSON

Shows practical patterns for serializing Java data to JSON format.

```java
// Create from Java collections
List<String> skills = List.of("Java", "PostgreSQL", "REST API");
Map<String, Object> preferences = Map.of(
  "theme", "dark",
  "language", "it",
  "notifications", true
);

JSONObject userJson = new JSONObject();
userJson.put("id", user.getId());
userJson.put("nome", user.getNome());
userJson.put("skills", new JSONArray(skills));
userJson.put("preferences", new JSONObject(preferences));
userJson.put("created_at", System.currentTimeMillis());

// Pretty print with indentation
String formatted = userJson.toString(2);
```

JSONObject and JSONArray constructors accept Java collections for easy conversion.

### REST API Integration Pattern

Real-world usage pattern for processing API requests and responses.

```java
// Parse incoming request
private PatientData parsePatientJSON(HttpServletRequest request) throws IOException {
  StringBuilder jsonInput = new StringBuilder();
  try (BufferedReader reader = request.getReader()) {
    String line;
    while ((line = reader.readLine()) != null) {
      jsonInput.append(line);
    }
  }
  
  JSONObject json = new JSONObject(jsonInput.toString());
  PatientData data = new PatientData();
  
  // Extract with validation
  data.nome = json.optString("nome", null);
  data.cognome = json.optString("cognome", null);
  
  if (json.has("data_nascita")) {
    data.dataNascita = Date.valueOf(json.getString("data_nascita"));
  }
  
  return data;
}

// Build response
private String buildResponse(PatientResult result) {
  JSONObject response = new JSONObject();
  response.put("result_code", result.resultCode);
  response.put("message", result.resultMessage);
  
  if (result.resultCode == 0) {
    JSONObject patient = new JSONObject();
    patient.put("id", result.idPaziente);
    patient.put("uuid", result.uuidPaziente);
    response.put("patient", patient);
  }
  
  return response.toString();
}
```

Common pattern for servlet-based REST APIs with proper error handling.

### Library Benefits

**Simple API**: Intuitive method names like put(), get(), has(), opt()
**Type Safety**: Automatic conversion with type-specific getters
**No Dependencies**: Self-contained library with no external requirements
**Error Handling**: Clear JSONException for malformed JSON
**Pretty Printing**: Built-in formatting with toString(indent)

### Maven Dependency

```xml
<dependency>
  <groupId>org.json</groupId>
  <artifactId>json</artifactId>
  <version>20240303</version>
</dependency>
```

### Expected JSON Processing Output

```
=== JSON Processing Examples ===

1. Basic JSON Object Operations:
  Nome: Mario
  Età: 42
  Attivo: true
  JSON: {"nome":"Mario","cognome":"Rossi","eta":42,"attivo":true,"note":null}

2. JSON Arrays and Nested Objects:
  Città: Milano
  Primo telefono: 02-1234567
  Numero telefoni: 2

3. JSON Parsing from String:
  Result: 0 - Success
  Patient ID: 12345
  Patient Name: Anna Bianchi

4. Converting Java Objects to JSON:
  User JSON with skills and preferences formatted

5. Error Handling and Safe Operations:
  JSONException caught: Invalid JSON format
  Safe access - existing field: value
  Optional string (with default): default_value
```

Shows comprehensive JSON operations from basic object creation to complex API integration patterns.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

---

&copy;2018-2025 Riccardo Vacirca. All right reserved.  
GNU GPL Version 2. See LICENSE