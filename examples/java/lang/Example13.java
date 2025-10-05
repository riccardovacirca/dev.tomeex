/* *****************************************************************************
 * Example13 - Collections: List
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example13.java
 * Run: java Example13
 * ---
 * First fruit: Apple
 * Apple
 * Banana
 * Cherry
 * Cherry is in the list
 * *****************************************************************************
*/

import java.util.ArrayList;
import java.util.List;

/** ListExample class demonstrating List usage */
class ListExample {
  private String buff = null;

  public ListExample() {
    List<String> fruits = new ArrayList<>();
    fruits.add("Apple");
    fruits.add("Banana");
    fruits.add("Cherry");

    this.buff = "First fruit: " + fruits.get(0) + "\n";
    
    for (String fruit : fruits) {
      this.buff += fruit + "\n";
    }
    
    fruits.remove("Banana");
    if (fruits.contains("Cherry")) {
      this.buff += "Cherry is in the list";
    }
  }

  public String toString() {
    return this.buff;
  }
}

public class Example13 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    ListExample example = new ListExample();
    System.out.printf("%s\n", example);
    
    System.out.printf("\n");
  }
}