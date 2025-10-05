/* *****************************************************************************
 * Example 03 - Arrays and Arrays utilities
 * (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example03.java
 * Run: java Example03
 * ---
 * [A, B, C]
 * [A, B, C]
 * [10, 20, 30]
 * [10, 20, 30]
 * [1.1, 2.2, 3.3]
 * [1.1, 2.2, 3.3]
 * Original: [30, 10, 20]
 * Sorted: [10, 20, 30]
 * Index of 20: 1
 * Copy extended: [10, 20, 30, 0, 0]
 * x equals y: true
 * x equals z: false
 * z after fill: [0, 0, 0]
 * *****************************************************************************
*/

class CharArray {
  private String buff = null;

  public CharArray() {
    {
      char[] x = {'A', 'B', 'C'};
      this.buff = String.format("[%c, %c, %c]", x[0], x[1], x[2]);
    }
    this.buff += "\n";
    {
      Character[] x = {'A', 'B', 'C'};
      this.buff += String.format("[%c, %c, %c]", x[0], x[1], x[2]);
    }
  }

  public String toString() {
    return this.buff;
  }
}

class IntArray
{
  private String buff = null;

  public IntArray() {
    {
      int[] x = {10, 20, 30};
      this.buff = String.format("[%d, %d, %d]", x[0], x[1], x[2]);
    }
    this.buff += "\n";
    {
      Integer[] x = {10, 20, 30};
      this.buff += String.format("[%d, %d, %d]", x[0], x[1], x[2]);
    }
  }

  public String toString() {
    return this.buff;
  }
}

class DoubleArray
{
  private String buff = null;

  public DoubleArray() {
    {
      double[] x = {1.1, 2.2, 3.3};
      this.buff = String.format("[%.1f, %.1f, %.1f]", x[0], x[1], x[2]);
    }
    this.buff += "\n";
    {
      Double[] x = {1.1, 2.2, 3.3};
      this.buff += String.format("[%.1f, %.1f, %.1f]", x[0], x[1], x[2]);
    }
  }

  public String toString() {
    return this.buff;
  }
}

class ArraysUtils
{
  private String buff = null;

  public ArraysUtils() {
    int[] x = {30, 10, 20};
    this.buff = "Original: " + java.util.Arrays.toString(x) + "\n";
    
    java.util.Arrays.sort(x);
    this.buff += "Sorted: " + java.util.Arrays.toString(x) + "\n";
    
    int found = java.util.Arrays.binarySearch(x, 20);
    this.buff += "Index of 20: " + found + "\n";
    
    int[] y = java.util.Arrays.copyOf(x, 5);
    this.buff += "Copy extended: " + java.util.Arrays.toString(y);
  }

  public String toString() {
    return this.buff;
  }
}

class ArraysComparison
{
  private String buff = null;

  public ArraysComparison() {
    int[] x = {1, 2, 3};
    int[] y = {1, 2, 3};
    int[] z = {3, 2, 1};
    
    this.buff = "x equals y: " + java.util.Arrays.equals(x, y) + "\n";
    this.buff += "x equals z: " + java.util.Arrays.equals(x, z) + "\n";
    
    java.util.Arrays.fill(z, 0);
    this.buff += "z after fill: " + java.util.Arrays.toString(z);
  }

  public String toString() {
    return this.buff;
  }
}

public class Example03
{
  public static void main(String[] args) {
    System.out.printf("%s\n", new CharArray());
    System.out.printf("%s\n", new IntArray());
    System.out.printf("%s\n", new DoubleArray());
    System.out.printf("%s\n", new ArraysUtils());
    System.out.printf("%s\n", new ArraysComparison());
  }
}