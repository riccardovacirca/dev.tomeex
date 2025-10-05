/* *****************************************************************************
 * Example 04 - Wrapper class methods
 * (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example04.java
 * Run: java Example04
 * ---
 * valueOf: A, charValue: A, toString: A
 * equals: true, compare: 0, compareTo: 0
 * isDigit: false, isLetter: true
 * toLowerCase: a, toUpperCase: A
 * valueOf: 42, intValue: 42, toString: 42
 * equals: true, compare: 0, compareTo: 0
 * valueOf: 3.14, doubleValue: 3.14, toString: 3.14
 * equals: true, compare: 0, compareTo: 0
 * *****************************************************************************
*/

class CharacterCreation
{
  private String buff = null;

  public CharacterCreation() {
    Character x = 'A';
    char y = x.charValue();
    String z = x.toString();
    this.buff = String.format(
      "valueOf: %c, charValue: %c, toString: %s",
      x, y, z
    );
  }

  public String toString() {
    return this.buff;
  }
}

class CharacterComparison
{
  private String buff = null;

  public CharacterComparison() {
    Character x = 'A';
    Character y = 'A';
    boolean eq = x.equals(y);
    int cmp1 = Character.compare('A', 'A');
    int cmp2 = x.compareTo(y);
    this.buff = String.format(
      "equals: %b, compare: %d, compareTo: %d",
      eq, cmp1, cmp2
    );
  }

  public String toString() {
    return this.buff;
  }
}

class CharacterValidation
{
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

class CharacterTransformation
{
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

class IntegerCreation
{
  private String buff = null;

  public IntegerCreation() {
    Integer x = 42;
    int y = x.intValue();
    String z = x.toString();
    this.buff = String.format("valueOf: %d, intValue: %d, toString: %s",
                              x, y, z);
  }

  public String toString() {
    return this.buff;
  }
}

class IntegerComparison
{
  private String buff = null;

  public IntegerComparison() {
    Integer x = 42;
    Integer y = 42;
    boolean eq = x.equals(y);
    int cmp1 = Integer.compare(42, 42);
    int cmp2 = x.compareTo(y);
    this.buff = String.format("equals: %b, compare: %d, compareTo: %d",
                              eq, cmp1, cmp2);
  }

  public String toString() {
    return this.buff;
  }
}

class DoubleCreation
{
  private String buff = null;

  public DoubleCreation() {
    Double x = 3.14;
    double y = x.doubleValue();
    String z = x.toString();
    this.buff = String.format("valueOf: %.2f, doubleValue: %.2f, toString: %s",
                              x, y, z);
  }

  public String toString() {
    return this.buff;
  }
}

class DoubleComparison
{
  private String buff = null;

  public DoubleComparison() {
    Double x = 3.14;
    Double y = 3.14;
    boolean eq = x.equals(y);
    int cmp1 = Double.compare(3.14, 3.14);
    int cmp2 = x.compareTo(y);
    this.buff = String.format("equals: %b, compare: %d, compareTo: %d",
                              eq, cmp1, cmp2);
  }

  public String toString() {
    return this.buff;
  }
}

public class Example04
{
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new CharacterCreation());
    System.out.printf("%s\n", new CharacterComparison());
    System.out.printf("%s\n", new CharacterValidation());
    System.out.printf("%s\n", new CharacterTransformation());
    System.out.printf("%s\n", new IntegerCreation());
    System.out.printf("%s\n", new IntegerComparison());
    System.out.printf("%s\n", new DoubleCreation());
    System.out.printf("%s\n", new DoubleComparison());
    System.out.printf("\n");
  }
}