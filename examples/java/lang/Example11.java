/* *****************************************************************************
 * Example11 - Static Members
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example11.java
 * Run: java Example11
 * ---
 * Initial count: 0
 * Count after creating 2 objects: 2
 * Square of 5 is 25
 * Config loaded: version 1.0.0
 * *****************************************************************************
*/

/** Counter class with static field */
class Counter {
  public static int count = 0;

  public Counter() {
    count++;
  }
}

/** MathHelper class with static methods */
class MathHelper {
  public static int square(int x) {
    return x * x;
  }
}

/** Config class with static block */
class Config {
  public static String VERSION;

  static {
    VERSION = "1.0.0";
    System.out.printf("Config loaded: version %s\n", VERSION);
  }
}

public class Example11 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    System.out.printf("Initial count: %d\n", Counter.count);
    new Counter();
    new Counter();
    System.out.printf("Count after creating 2 objects: %d\n", Counter.count);

    int sq = MathHelper.square(5);
    System.out.printf("Square of 5 is %d\n", sq);

    // Trigger static block
    String v = Config.VERSION;
    
    System.out.printf("\n");
  }
}