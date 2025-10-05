/* *****************************************************************************
 * Example 01 - Simple HelloWorld message
 * (c)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example01.java
 * Run: java Example01
 * ---
 * Hello, World!
 * *****************************************************************************
*/

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
    HelloWorld helloWorld = new HelloWorld();
    System.out.printf("%s\n", helloWorld); // otherwise "%s\n", new HelloWorld()
  }
}
