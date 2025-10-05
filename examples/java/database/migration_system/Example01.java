
// Database Migration Manager Example

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Migration interface
interface Migration {
  String getVersion();
  String getDescription();
  void execute();
}

// Sample migrations
class CreateUsersTableMigration implements Migration {
  @Override
  public String getVersion() { return "20240101_001"; }
  @Override
  public String getDescription() { return "Create users table"; }
  @Override
  public void execute() {
    System.out.println("  SQL: CREATE TABLE users (id SERIAL, name VARCHAR(255))");
  }
}

class AddUsersIndexMigration implements Migration {
  @Override
  public String getVersion() { return "20240101_002"; }
  @Override
  public String getDescription() { return "Add index on users.name"; }
  @Override
  public void execute() {
    System.out.println("  SQL: CREATE INDEX idx_users_name ON users(name)");
  }
}

class AddUsersEmailMigration implements Migration {
  @Override
  public String getVersion() { return "20240102_001"; }
  @Override
  public String getDescription() { return "Add email column to users"; }
  @Override
  public void execute() {
    System.out.println("  SQL: ALTER TABLE users ADD COLUMN email VARCHAR(255)");
  }
}

// Migration manager
class MigrationManager {
  private Set<String> appliedMigrations = new HashSet<>();
  public void runMigrations() {
    System.out.println("Starting database migrations...\n");
    // Simulate loading applied migrations from database
    loadAppliedMigrations();
    // Get all available migrations
    List<Migration> availableMigrations = getAvailableMigrations();
    // Apply missing migrations
    for (Migration migration : availableMigrations) {
      if (!appliedMigrations.contains(migration.getVersion())) {
        applyMigration(migration);
      } else {
        System.out.printf("SKIP: %s - %s (already applied)%n", 
          migration.getVersion(), migration.getDescription());
      }
    }
    System.out.println("\nDatabase migrations completed!");
  }

  private void loadAppliedMigrations() {
    // Simulate some migrations already applied
    appliedMigrations.add("20240101_001");
    System.out.println("Loaded applied migrations: " + appliedMigrations);
    System.out.println();
  }

  private List<Migration> getAvailableMigrations() {
    List<Migration> migrations = new ArrayList<>();
    migrations.add(new CreateUsersTableMigration());
    migrations.add(new AddUsersIndexMigration());
    migrations.add(new AddUsersEmailMigration());
    return migrations;
  }

  private void applyMigration(Migration migration) {
    System.out.printf("APPLYING: %s - %s%n", 
      migration.getVersion(), migration.getDescription());
    long startTime = System.currentTimeMillis();
    try {
      // Execute migration
      migration.execute();
      // Record as applied
      appliedMigrations.add(migration.getVersion());
      long executionTime = System.currentTimeMillis() - startTime;
      System.out.printf("SUCCESS: Applied in %d ms%n%n", executionTime);
    } catch (Exception e) {
      System.out.printf("ERROR: Migration failed - %s%n%n", e.getMessage());
    }
  }
}

// Application startup listener simulation
class DatabaseMigrationListener {
  public void onApplicationStart() {
    System.out.println("=== Application Starting ===");
    System.out.println("Running database migrations...\n");
    MigrationManager manager = new MigrationManager();
    manager.runMigrations();
    System.out.println("=== Application Ready ===");
  }
}

// Main example
public class Example01 {
  public static void main(String[] args) {
    System.out.println("=== Database Migration Manager Example ===\n");
    // Simulate application startup
    DatabaseMigrationListener listener = new DatabaseMigrationListener();
    listener.onApplicationStart();
  }
}