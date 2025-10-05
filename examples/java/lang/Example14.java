/* *****************************************************************************
 * Example14 - Collections: Map
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example14.java
 * Run: java Example14
 * ---
 * Score of Alice: 85
 * Alice -> 85
 * Bob -> 92
 * Charlie -> 78
 * Bob is in the map with score 92
 * Someone scored 78
 * *****************************************************************************
*/

import java.util.HashMap;
import java.util.Map;

/** MapExample class demonstrating Map usage */
class MapExample {
  private String buff = null;

  public MapExample() {
    Map<String, Integer> scores = new HashMap<>();
    scores.put("Alice", 85);
    scores.put("Bob", 92);
    scores.put("Charlie", 78);

    this.buff = "Score of Alice: " + scores.get("Alice") + "\n";
    
    for (Map.Entry<String, Integer> entry : scores.entrySet()) {
      this.buff += entry.getKey() + " -> " + entry.getValue() + "\n";
    }
    
    if (scores.containsKey("Bob")) {
      this.buff += "Bob is in the map with score " + scores.get("Bob") + "\n";
    }

    if (scores.containsValue(78)) {
      this.buff += "Someone scored 78";
    }
  }

  public String toString() {
    return this.buff;
  }
}

public class Example14 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    MapExample example = new MapExample();
    System.out.printf("%s\n", example);
    
    System.out.printf("\n");
  }
}