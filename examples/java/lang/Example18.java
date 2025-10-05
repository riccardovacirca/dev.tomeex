/* *****************************************************************************
 * Example18 - Event Listeners
 * (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example18.java
 * Run: java Example18
 * ---
 * === Simple Event Listener Example ===
 * 
 * [Logger1] Event: APPLICATION_STARTED at [timestamp]
 * [Logger2] Event: APPLICATION_STARTED at [timestamp]
 * [Logger1] Event: USER_LOGIN at [timestamp]
 * [Logger2] Event: USER_LOGIN at [timestamp]
 * [Logger1] Event: DATA_PROCESSED at [timestamp]
 * [Logger2] Event: DATA_PROCESSED at [timestamp]
 * [Logger1] Event: APPLICATION_STOPPED at [timestamp]
 * [Logger2] Event: APPLICATION_STOPPED at [timestamp]
 * *****************************************************************************
*/

import java.util.ArrayList;
import java.util.List;

// Simple event class
class AppEvent {
  private String eventType;
  private long timestamp;
  public AppEvent(String eventType) {
    this.eventType = eventType;
    this.timestamp = System.currentTimeMillis();
  }
  public String getEventType() { return eventType; }
  public long getTimestamp() { return timestamp; }
}

// Event listener interface
interface AppEventListener {
  void onEvent(AppEvent event);
}

// Simple event listener implementation
class LoggingListener implements AppEventListener {
  private String name;
  public LoggingListener(String name) {
    this.name = name;
  }
  @Override
  public void onEvent(AppEvent event) {
    System.out.printf("[%s] Event: %s at %d%n", 
      name, event.getEventType(), event.getTimestamp());
  }
}

// Event publisher
class EventPublisher {
  private List<AppEventListener> listeners = new ArrayList<>();
  public void addListener(AppEventListener listener) {
    listeners.add(listener);
  }
  public void publishEvent(String eventType) {
    AppEvent event = new AppEvent(eventType);
    for (AppEventListener listener : listeners) {
      listener.onEvent(event);
    }
  }
}

// Main example
public class Example18 {
  public static void main(String[] args) {
    System.out.println("=== Simple Event Listener Example ===\n");
    // Create event publisher
    EventPublisher publisher = new EventPublisher();
    // Add listeners
    publisher.addListener(new LoggingListener("Logger1"));
    publisher.addListener(new LoggingListener("Logger2"));
    // Publish events
    publisher.publishEvent("APPLICATION_STARTED");
    publisher.publishEvent("USER_LOGIN");
    publisher.publishEvent("DATA_PROCESSED");
    publisher.publishEvent("APPLICATION_STOPPED");
  }
}