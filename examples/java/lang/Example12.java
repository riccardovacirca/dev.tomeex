/* *****************************************************************************
 * Example12 - Enums
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example12.java
 * Run: java Example12
 * ---
 * Today is MONDAY
 * Status: IN_PROGRESS, code: 2
 * *****************************************************************************
*/

/** Day enum */
enum Day {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

/** Schedule class using Day enum */
class Schedule {
  private Day day;

  public Schedule(Day day) {
    this.day = day;
  }

  public String getMessage() {
    return "Today is " + this.day;
  }
}

/** Status enum with fields and methods */
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

public class Example12 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    Schedule sched = new Schedule(Day.MONDAY);
    System.out.printf("%s\n", sched.getMessage());

    Status s = Status.IN_PROGRESS;
    System.out.printf("Status: %s, code: %d\n", s, s.getCode());
    
    System.out.printf("\n");
  }
}