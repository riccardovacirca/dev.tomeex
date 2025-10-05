/* *****************************************************************************
 * Example09 - Interfaces
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example09.java
 * Run: java Example09
 * ---
 * Hello, Alice!
 * Hi, Bob
 * LOG: Greeting sent to Bob
 * *****************************************************************************
*/

/** Greeter interface */
interface Greeter {
  String greet(String name);
}

/** EnglishGreeter class implementing Greeter */
class EnglishGreeter implements Greeter {
  public String greet(String name) {
    return "Hello, " + name + "!";
  }
}

/** Logger interface */
interface Logger {
  void log(String message);
}

/** ConsoleGreeter class implementing multiple interfaces */
class ConsoleGreeter implements Greeter, Logger {
  public String greet(String name) {
    return "Hi, " + name;
  }

  public void log(String message) {
    System.out.printf("LOG: %s\n", message);
  }
}

public class Example09 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    EnglishGreeter english = new EnglishGreeter();
    System.out.printf("%s\n", english.greet("Alice"));
    
    ConsoleGreeter console = new ConsoleGreeter();
    System.out.printf("%s\n", console.greet("Bob"));
    console.log("Greeting sent to Bob");
    
    System.out.printf("\n");
  }
}