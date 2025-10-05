/* *****************************************************************************
 * Example 02 - Primitive types and wrappers
 * (c)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example02.java
 * Run: java Example02
 * ---
 * x = 0, y = 1, z = 2
 * x = 0, y = 1, z = 2
 * x = 1, y = 2, z = 3
 * x = 1, y = 2, z = 3
 * x = 1.1, y = 2.2, z = 3.3
 * x = 1.1, y = 2.2, z = 3.3
 * *****************************************************************************
*/

class CharType
{
  private String buff = null;

  public CharType() {
    {
      char x = 'A', y = 'B', z = 'C';
      this.buff = String.format("x = %c, y = %c, z = %c", x, y, z);
    }
    this.buff += "\n";
    {
      Character x = 'A', y = 'B', z = 'C';
      this.buff += String.format("x = %c, y = %c, z = %c", x, y, z);
    }
  }

  public String toString() {
    return this.buff;
  }
}

class IntType
{
  private String buff = null;

  public IntType() {
    {
      int x = 1, y = 2, z = 3;
      this.buff = String.format("x = %d, y = %d, z = %d", x, y, z);
    }
    this.buff += "\n";
    {
      Integer x = 1, y = 2, z = 3;
      this.buff += String.format("x = %d, y = %d, z = %d", x, y, z);
    }
  }

  public String toString() {
    return this.buff;
  }
}

class DoubleType
{
  private String buff = null;

  public DoubleType() {
    {
      double x = 1.1, y = 2.2, z = 3.3;
      this.buff = String.format("x = %.1f, y = %.1f, z = %.1f", x, y, z);
    }
    this.buff += "\n";
    {
      Double x = 1.1, y = 2.2, z = 3.3;
      this.buff += String.format("x = %.1f, y = %.1f, z = %.1f", x, y, z);
    }
  }

  public String toString() {
    return this.buff;
  }
}

public class Example02
{
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new CharType());
    System.out.printf("%s\n", new IntType());
    System.out.printf("%s\n", new DoubleType());
    System.out.printf("\n");
  }
}
