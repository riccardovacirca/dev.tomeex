/* *****************************************************************************
 * Example17 - Memory Management
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example17.java
 * Run: java Example17
 * ---
 * Stack primitive: 42
 * Heap object: Hello World
 * Reference equality (string pool): true
 * Reference equality (new objects): false
 * Content equality: true
 * Method call depth: 3
 * StringBuilder efficient: Hello World Java Programming 
 * ArrayList reuse demonstration completed
 * Cache size after cleanup: 1000
 * Static inner class created without outer reference
 * Memory demonstration completed
 * *****************************************************************************
*/

import java.util.ArrayList;
import java.util.List;

/** Memory allocation demonstration */
class MemoryDemo {
    public void stackVsHeap() {
        // Stack allocation - primitives
        int primitive = 42;           // Stored directly on stack
        boolean flag = true;          // Stored directly on stack
        char letter = 'A';            // Stored directly on stack
        
        // Heap allocation - objects
        String text = "Hello World";  // Reference on stack, object on heap
        Integer wrapper = 100;        // Reference on stack, object on heap
        int[] array = {1, 2, 3};     // Reference on stack, array on heap
        
        System.out.printf("Stack primitive: %d\n", primitive);
        System.out.printf("Heap object: %s\n", text);
    }
}

/** Object references demonstration */
class ReferenceDemo {
    public void referenceVsValue() {
        // Same object reference - string pool
        String str1 = "Hello";
        String str2 = "Hello";        // String pool - same reference
        
        // Different object references
        String str3 = new String("Hello");  // Force new object on heap
        String str4 = new String("Hello");  // Another new object on heap
        
        // Reference comparison vs content comparison
        boolean sameRef1 = (str1 == str2);     // true - same reference
        boolean sameRef2 = (str3 == str4);     // false - different references
        boolean sameContent = str3.equals(str4); // true - same content
        
        System.out.printf("Reference equality (string pool): %b\n", sameRef1);
        System.out.printf("Reference equality (new objects): %b\n", sameRef2);
        System.out.printf("Content equality: %b\n", sameContent);
    }
}

/** Stack frame demonstration */
class StackFrameDemo {
    private int depth = 0;
    
    public void methodA() {
        depth++;
        int localVar = 10;            // Stack frame for methodA
        String localObj = "A";        // Reference in stack, object in heap
        methodB(localVar);            // New stack frame created
    }
    
    public void methodB(int param) {
        depth++;
        int anotherVar = param * 2;   // Stack frame for methodB
        String anotherObj = "B";      // New reference and object
        methodC();                    // Another stack frame
    }
    
    public void methodC() {
        depth++;
        // Deepest stack frame
        int deepVar = 100;
        System.out.printf("Method call depth: %d\n", depth);
        depth = 0; // Reset for demonstration
    }
    // When methods return, stack frames are popped
}

/** Garbage collection demonstration */
class GarbageCollectionDemo {
    public void gcExample() {
        // Object creation
        StringBuilder sb1 = new StringBuilder("Initial");
        StringBuilder sb2 = new StringBuilder("Second");
        
        // sb1 is eligible for GC after this point
        sb1 = null;
        
        // Reassignment makes original sb2 eligible for GC
        sb2 = new StringBuilder("Third");
        
        // Local method scope - all local references become
        // eligible for GC when method ends
    }
    
    public String createAndReturn() {
        String temp = "Temporary";    // Local reference
        return temp;                  // Object survives method return
    }
}

/** Memory optimization techniques */
class MemoryOptimization {
    // String concatenation - inefficient
    public String inefficientConcat(String[] words) {
        String result = "";
        for (String word : words) {
            result += word + " ";     // Creates new String objects each time
        }
        return result;
    }
    
    // String concatenation - efficient
    public String efficientConcat(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word).append(" "); // Reuses same buffer
        }
        return sb.toString();
    }
    
    // Object reuse vs recreation
    public void objectReuse() {
        // Inefficient - creates new objects (commented out for demo)
        // for (int i = 0; i < 1000; i++) {
        //     List<String> list = new ArrayList<>(); // New object each iteration
        // }
        
        // More efficient - reuse object
        List<String> reusableList = new ArrayList<>();
        for (int i = 0; i < 100; i++) { // Reduced for demo
            reusableList.clear();      // Reuse same object
            reusableList.add("Item " + i);
        }
        
        System.out.printf("ArrayList reuse demonstration completed\n");
    }
}

/** Memory leak prevention */
class MemoryLeakDemo {
    private List<String> cache = new ArrayList<>();
    
    // Memory leak - cache grows indefinitely
    public void leakyCache(String data) {
        cache.add(data);              // Never removed
    }
    
    // Fixed - with size limit
    public void boundedCache(String data) {
        if (cache.size() > 1000) {
            cache.remove(0);          // Remove oldest entry
        }
        cache.add(data);
    }
    
    public void demonstrateCache() {
        // Fill cache beyond limit
        for (int i = 0; i < 1500; i++) {
            boundedCache("Data " + i);
        }
        System.out.printf("Cache size after cleanup: %d\n", cache.size());
    }
    
    // Inner class memory leak
    public class InnerClass {
        // Holds implicit reference to outer class
        private String data;
        
        public InnerClass(String data) {
            this.data = data;
        }
    }
    
    // Fixed with static inner class
    public static class StaticInnerClass {
        // No implicit reference to outer class
        private String data;
        
        public StaticInnerClass(String data) {
            this.data = data;
        }
    }
}

public class Example17 {
    public static void main(String[] args) {
        System.out.printf("\n");
        
        // Stack vs Heap allocation
        MemoryDemo memoryDemo = new MemoryDemo();
        memoryDemo.stackVsHeap();
        
        // Object references
        ReferenceDemo refDemo = new ReferenceDemo();
        refDemo.referenceVsValue();
        
        // Method call stack
        StackFrameDemo stackDemo = new StackFrameDemo();
        stackDemo.methodA();
        
        // Memory optimization
        MemoryOptimization optimization = new MemoryOptimization();
        String[] words = {"Hello", "World", "Java", "Programming"};
        String efficient = optimization.efficientConcat(words);
        System.out.printf("StringBuilder efficient: %s\n", efficient.trim());
        
        optimization.objectReuse();
        
        // Memory leak prevention
        MemoryLeakDemo leakDemo = new MemoryLeakDemo();
        leakDemo.demonstrateCache();
        
        // Static inner class demonstration
        MemoryLeakDemo.StaticInnerClass staticInner = 
            new MemoryLeakDemo.StaticInnerClass("No outer reference");
        System.out.printf("Static inner class created without outer reference\n");
        
        // Garbage collection suggestion (not guaranteed to run)
        System.gc(); // Suggest garbage collection
        
        System.out.printf("Memory demonstration completed\n");
        System.out.printf("\n");
    }
}