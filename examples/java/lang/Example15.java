/* *****************************************************************************
 * Example15 - Annotations: Basic Usage
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example15.java
 * Run: java Example15
 * ---
 * Woof!
 * This method is outdated
 * Use this method instead
 * *****************************************************************************
*/

/** Animal class */
class Animal {
  public String speak() {
    return "Some generic sound";
  }
}

/** Dog class extending Animal */
class Dog extends Animal {
  @Override
  public String speak() {
    return "Woof!";
  }
}

/** OldLibrary class with deprecated method */
class OldLibrary {
  @Deprecated
  public void oldMethod() {
    System.out.printf("This method is outdated\n");
  }

  public void newMethod() {
    System.out.printf("Use this method instead\n");
  }
}

public class Example15 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    Dog dog = new Dog();
    System.out.printf("%s\n", dog.speak());

    OldLibrary lib = new OldLibrary();
    lib.oldMethod();  // Compiler warning: method is deprecated
    lib.newMethod();
    
    System.out.printf("\n");
  }
}