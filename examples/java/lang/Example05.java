/* *****************************************************************************
 * Example 05 - Code blocks
 * (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example05.java
 * Run: java Example05
 * ---
 * Static block executed
 * Instance block executed
 * Constructor called
 * Method block executed
 * Local block executed
 * *****************************************************************************
*/

class ClassBlock
{
  private String buff = null;

  static {
    System.out.printf("Static block executed\n");
  }

  public ClassBlock() {
    this.buff = "Static block executed";
  }

  public String toString() {
    return this.buff;
  }
}

class InstanceBlock
{
  private String buff = null;

  {
    this.buff = "Instance block executed";
  }

  public InstanceBlock() {
    // Constructor called after instance block
  }

  public String toString() {
    return this.buff;
  }
}

class ConstructorBlock
{
  private String buff = null;

  {
    this.buff = "Instance block executed\n";
  }

  public ConstructorBlock() {
    this.buff += "Constructor called";
  }

  public String toString() {
    return this.buff;
  }
}

class MethodBlock
{
  private String buff = null;

  public MethodBlock() {
    this.executeMethodBlock();
  }

  private void executeMethodBlock() {
    {
      String temp = "Method block executed";
      this.buff = temp;
    }
  }

  public String toString() {
    return this.buff;
  }
}

class LocalBlock
{
  private String buff = null;

  public LocalBlock() {
    String outer = "Local";
    {
      String inner = " block executed";
      this.buff = outer + inner;
    }
  }

  public String toString() {
    return this.buff;
  }
}

public class Example05
{
  public static void main(String[] args) {
    System.out.printf("\n");
    System.out.printf("%s\n", new ClassBlock());
    System.out.printf("%s\n", new InstanceBlock());
    System.out.printf("%s\n", new ConstructorBlock());
    System.out.printf("%s\n", new MethodBlock());
    System.out.printf("%s\n", new LocalBlock());
    System.out.printf("\n");
  }
}