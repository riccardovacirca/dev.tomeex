/* *****************************************************************************
 * Example10 - Packages and Imports
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example10.java
 * Run: java Example10
 * ---
 * Square of 5 is 25
 * Square of 3 is 9
 * *****************************************************************************
*/

/** MathUtils class in util package */
class MathUtils {
  public static int square(int x) {
    return x * x;
  }
}

/** MainApp class demonstrating package usage */
class MainApp {
  private String buff = null;

  public MainApp() {
    int num = 5;
    int sq = MathUtils.square(num);
    this.buff = "Square of " + num + " is " + sq;
  }

  public String toString() {
    return this.buff;
  }
}

/** FullyQualifiedExample class */
class FullyQualifiedExample {
  private String buff = null;

  public FullyQualifiedExample() {
    int num = 3;
    int sq = MathUtils.square(num);
    this.buff = "Square of " + num + " is " + sq;
  }

  public String toString() {
    return this.buff;
  }
}

public class Example10 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    MainApp app = new MainApp();
    System.out.printf("%s\n", app);
    
    FullyQualifiedExample example = new FullyQualifiedExample();
    System.out.printf("%s\n", example);
    
    System.out.printf("\n");
  }
}