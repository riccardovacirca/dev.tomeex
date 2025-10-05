/* *****************************************************************************
 * Example08 - Inheritance
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example08.java
 * Run: java Example08
 * ---
 * Animal name: Rex
 * Rex barks loudly
 * Cat name: Milo
 * *****************************************************************************
*/

/** Animal base class */
class Animal {
  protected String name;

  public Animal(String name) {
    this.name = name;
  }

  public String info() {
    return "Animal name: " + this.name;
  }
}

/** Dog subclass */
class Dog extends Animal {
  public Dog(String name) {
    super(name);
  }

  public String bark() {
    return this.name + " barks loudly";
  }
}

/** Cat subclass */
class Cat extends Animal {
  public Cat(String name) {
    super(name);
  }

  @Override
  public String info() {
    return "Cat name: " + this.name;
  }
}

public class Example08 {
  public static void main(String[] args) {
    System.out.printf("\n");
    Dog rex = new Dog("Rex");
    System.out.printf("%s\n", rex.info());
    System.out.printf("%s\n", rex.bark());
    Cat milo = new Cat("Milo");
    System.out.printf("%s\n", milo.info());
    System.out.printf("\n");
  }
}