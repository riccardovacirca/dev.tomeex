
/* *****************************************************************************
 * Example19 - Lambda Expressions
 * (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example19.java
 * Run: java Example19
 * ---
 * === Lambda Expressions Example ===
 * 
 * Traditional worker: started and finished
 * Lambda worker dice: passo 1
 * Lambda worker dice: passo 2
 * Lambda worker dice: passo 3
 * Lambda worker terminato
 * 
 * === Lambda with Functional Interfaces ===
 * 
 * Addition: 5 + 3 = 8
 * Multiplication: 5 * 3 = 15
 * Processed: HELLO WORLD!
 * 
 * Main terminato
 * *****************************************************************************
*/

// Functional interfaces for lambda examples
@FunctionalInterface
interface MathOperation {
  int operate(int a, int b);
}

@FunctionalInterface
interface StringProcessor {
  String process(String input);
}

class ThreadComparison {
  private String buff = null;

  public ThreadComparison() {
    this.buff = "=== Lambda Expressions Example ===\n\n";
    
    // Traditional anonymous inner class approach
    Thread traditionalWorker = new Thread(new Runnable() {
      @Override
      public void run() {
        buff += "Traditional worker: started and finished\n";
      }
    });
    
    // Lambda expression approach - much more concise
    Thread lambdaWorker = new Thread(() -> {
      for (int i = 1; i <= 3; i++) {
        buff += "Lambda worker dice: passo " + i + "\n";
        try {
          Thread.sleep(100); // Reduced sleep for faster execution
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
      buff += "Lambda worker terminato\n";
    });
    
    // Execute both threads
    traditionalWorker.start();
    lambdaWorker.start();
    
    // Wait for completion
    try {
      traditionalWorker.join();
      lambdaWorker.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public String toString() {
    return this.buff;
  }
}

class FunctionalInterfaceExamples {
  private String buff = null;

  public FunctionalInterfaceExamples() {
    this.buff = "\n=== Lambda with Functional Interfaces ===\n\n";
    
    // Simple lambda operations
    MathOperation addition = (a, b) -> a + b;
    MathOperation multiplication = (a, b) -> a * b;
    
    this.buff += "Addition: 5 + 3 = " + addition.operate(5, 3) + "\n";
    this.buff += "Multiplication: 5 * 3 = " + multiplication.operate(5, 3) + "\n";
    
    // Multi-statement lambda
    StringProcessor processor = (str) -> {
      String result = str.toUpperCase().trim();
      return result + "!";
    };
    
    this.buff += "Processed: " + processor.process("  hello world  ") + "\n";
    this.buff += "\nMain terminato";
  }

  public String toString() {
    return this.buff;
  }
}

public class Example19 {
  public static void main(String[] args) {
    System.out.printf("%s", new ThreadComparison());
    System.out.printf("%s\n", new FunctionalInterfaceExamples());
  }
}