/* *****************************************************************************
 * Example20 - Application Lifecycle Threading
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example20.java
 * Run: java Example20
 * ---
 * === Application Lifecycle Threading Example ===
 * 
 * === Application Starting ===
 * Background worker started
 * Background service started
 * 
 * Application running... (will stop after 10 seconds)
 * 
 * Worker attivo...
 * Worker attivo...
 * Worker attivo...
 * 
 * === Application Stopping ===
 * Worker terminato
 * Application stopped
 * 
 * Main application terminated
 * *****************************************************************************
*/

import java.util.concurrent.atomic.AtomicBoolean;

// Background service that runs throughout application lifecycle
class BackgroundService {
  private Thread workerThread;
  private final AtomicBoolean running = new AtomicBoolean(false);
  public void start() {
    if (running.compareAndSet(false, true)) {
      // Create background thread with lambda expression
      workerThread = new Thread(() -> {
        System.out.println("Background worker started");
        while (running.get()) {
          try {
            // Simulate background work
            System.out.println("Worker attivo...");
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            System.out.println("Worker interrupted");
            Thread.currentThread().interrupt();
            break;
          }
        }
        System.out.println("Worker terminato");
      });
      workerThread.start();
    }
  }
  
  public void stop() {
    if (running.compareAndSet(true, false)) {
      if (workerThread != null) {
        // Signal thread to stop and interrupt if necessary
        workerThread.interrupt();
        try {
          // Wait for thread to finish gracefully
          workerThread.join(5000); // 5 second timeout
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}

// Application lifecycle listener - simplified version
class BackgroundThreadListener {
  private BackgroundService backgroundService;
  public void onApplicationStart() {
    System.out.println("=== Application Starting ===");
    backgroundService = new BackgroundService();
    backgroundService.start();
    // Add shutdown hook for graceful termination
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("\nShutdown hook triggered");
      if (backgroundService != null) {
        backgroundService.stop();
      }
    }));
    System.out.println("Background service started");
  }
  public void onApplicationStop() {
    System.out.println("\n=== Application Stopping ===");
    if (backgroundService != null) {
      backgroundService.stop();
    }
    System.out.println("Application stopped");
  }
}

// Main application class
public class Example20 {
  public static void main(String[] args) {
    System.out.println("=== Application Lifecycle Threading Example ===\n");
    // Create lifecycle listener
    BackgroundThreadListener listener = new BackgroundThreadListener();
    // Simulate application startup
    listener.onApplicationStart();
    try {
      // Let the application run for a while
      System.out.println("\nApplication running... (will stop after 10 seconds)\n");
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    // Simulate application shutdown
    listener.onApplicationStop();
    System.out.println("\nMain application terminated");
  }
}