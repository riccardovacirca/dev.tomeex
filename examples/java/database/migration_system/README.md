<!-- ======================================================================= -->

# Database Migration System Tutorial

<!-- ======================================================================= -->

## Table of Contents

- [Migration Manager Pattern](#migration-manager-pattern)
- [Migration Interface](#migration-interface)
- [Version Control System](#version-control-system)
- [Execution Flow](#execution-flow)
- [Application Lifecycle Integration](#application-lifecycle-integration)

<!-- ======================================================================= -->

## Migration Manager Pattern

Source file: `examples/database/migration_system/Example01.java`

This example demonstrates an automated database migration system that manages database schema evolution during Java application startup. The pattern provides version control for database changes, ensuring consistent and repeatable deployments across different environments. The system tracks applied migrations, executes pending changes in chronological order, and maintains atomicity through proper transaction handling.

### Migration Interface Definition

All database migrations implement a common interface for consistency and predictable execution.

```java
interface Migration {
    String getVersion();        // Version identifier (e.g., "20240101_001")
    String getDescription();    // Human-readable description
    void execute();            // Migration logic
}
```

The interface defines three essential methods: version identification for ordering, human-readable descriptions for tracking, and execution logic for the actual database changes.

### Concrete Migration Implementation

Each migration represents a specific database change with unique versioning and executable SQL operations.

```java
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
```

Concrete migrations encapsulate specific database changes, using timestamp-based versioning for proper execution ordering.

### Migration Manager Core Logic

The migration manager orchestrates the entire migration process with state tracking and error handling.

```java
class MigrationManager {
    private Set<String> appliedMigrations = new HashSet<>();
    
    public void runMigrations() {
        loadAppliedMigrations();
        List<Migration> availableMigrations = getAvailableMigrations();
        
        for (Migration migration : availableMigrations) {
            if (!appliedMigrations.contains(migration.getVersion())) {
                applyMigration(migration);
            }
        }
    }
}
```

The manager maintains a registry of applied migrations and compares against available migrations to determine which changes need execution.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Migration Interface

Source file: `examples/database/migration_system/Example01.java`

The migration interface provides a contract that ensures all database changes follow consistent patterns. This standardization enables automated processing, proper error handling, and maintainable code organization across all database evolution tasks.

### Version Identification

Each migration must provide a unique version identifier following timestamp conventions.

```java
public String getVersion() { 
    return "20240101_001"; 
}
```

Version identifiers use YYYYMMDD_NNN format to ensure chronological ordering and prevent version conflicts during development.

### Descriptive Documentation

Migrations include human-readable descriptions for tracking and debugging purposes.

```java
public String getDescription() { 
    return "Create users table"; 
}
```

Descriptions appear in logs and migration history, providing context for database changes during troubleshooting.

### Executable Logic

The execute method contains the actual database operations for the migration.

```java
public void execute() {
    System.out.println("  SQL: CREATE TABLE users (id SERIAL, name VARCHAR(255))");
}
```

In real implementations, this method would execute SQL statements against the database connection.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Version Control System

Source file: `examples/database/migration_system/Example01.java`

The migration system uses timestamp-based versioning to ensure proper execution order and prevent conflicts between multiple developers working on database changes. This approach provides predictable behavior and maintains consistency across different environments.

### Timestamp-Based Versioning

Migration versions follow the YYYYMMDD_NNN pattern for chronological ordering.

```java
// Examples of proper version identifiers
"20240101_001"  // First migration on January 1, 2024
"20240101_002"  // Second migration on the same day
"20240102_001"  // First migration on January 2, 2024
```

### Migration Registration

Available migrations are registered in the manager for execution tracking.

```java
private List<Migration> getAvailableMigrations() {
    List<Migration> migrations = new ArrayList<>();
    migrations.add(new CreateUsersTableMigration());
    migrations.add(new AddUsersIndexMigration());
    migrations.add(new AddUsersEmailMigration());
    return migrations;
}
```

The registration order doesn't matter because versions determine execution sequence automatically.

### Applied Migration Tracking

The system maintains a record of successfully applied migrations to prevent duplicate execution.

```java
private void loadAppliedMigrations() {
    appliedMigrations.add("20240101_001");
    System.out.println("Loaded applied migrations: " + appliedMigrations);
}
```

In production systems, this information persists in a dedicated schema_migrations table.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Execution Flow

Source file: `examples/database/migration_system/Example01.java`

The migration execution follows a systematic process that ensures reliability, atomicity, and proper error handling. Each step builds upon the previous one to create a robust database evolution system.

### Migration Processing Sequence

The system follows a predictable execution sequence for consistent behavior.

1. **Load Applied Migrations**: Retrieve list of previously executed migrations from database
2. **Get Available Migrations**: Collect all migration classes available in the system
3. **Compare and Filter**: Determine which migrations need execution
4. **Execute in Order**: Run pending migrations according to version sequence
5. **Record Success**: Mark successfully applied migrations for future reference

### Individual Migration Execution

Each migration executes within proper error handling and timing measurement.

```java
private void applyMigration(Migration migration) {
    System.out.printf("APPLYING: %s - %s%n", 
        migration.getVersion(), migration.getDescription());
    
    long startTime = System.currentTimeMillis();
    
    try {
        migration.execute();
        appliedMigrations.add(migration.getVersion());
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("SUCCESS: Applied in %d ms%n%n", executionTime);
    } catch (Exception e) {
        System.out.printf("ERROR: Migration failed - %s%n%n", e.getMessage());
    }
}
```

### Skip Logic for Applied Migrations

Previously applied migrations are skipped to maintain idempotency.

```java
if (!appliedMigrations.contains(migration.getVersion())) {
    applyMigration(migration);
} else {
    System.out.printf("SKIP: %s - %s (already applied)%n", 
        migration.getVersion(), migration.getDescription());
}
```

This prevents accidental re-execution of database changes that could cause errors or data corruption.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

## Application Lifecycle Integration

Source file: `examples/database/migration_system/Example01.java`

The migration system integrates with application lifecycle events to ensure database readiness before application services start. This pattern is commonly implemented using ServletContextListener in web applications or similar lifecycle hooks in other frameworks.

### Database Migration Listener

Application startup triggers automatic migration execution through lifecycle event handling.

```java
class DatabaseMigrationListener {
    public void onApplicationStart() {
        System.out.println("=== Application Starting ===");
        System.out.println("Running database migrations...\n");
        
        MigrationManager manager = new MigrationManager();
        manager.runMigrations();
        
        System.out.println("=== Application Ready ===");
    }
}
```

### Integration with Application Bootstrap

The migration system executes during application initialization, before business logic activation.

```java
public static void main(String[] args) {
    System.out.println("=== Database Migration Manager Example ===\n");
    
    DatabaseMigrationListener listener = new DatabaseMigrationListener();
    listener.onApplicationStart();
}
```

### Expected Migration System Output

```
=== Database Migration Manager Example ===

=== Application Starting ===
Running database migrations...

Starting database migrations...

Loaded applied migrations: [20240101_001]

SKIP: 20240101_001 - Create users table (already applied)
APPLYING: 20240101_002 - Add index on users.name
  SQL: CREATE INDEX idx_users_name ON users(name)
SUCCESS: Applied in 2 ms

APPLYING: 20240102_001 - Add email column to users
  SQL: ALTER TABLE users ADD COLUMN email VARCHAR(255)
SUCCESS: Applied in 1 ms

Database migrations completed!
=== Application Ready ===
```

Shows the complete migration execution flow with applied migration tracking and timing information.

[↑ Back to Contents](#table-of-contents)

<!-- ======================================================================= -->

---

&copy;2018-2025 Riccardo Vacirca. All right reserved.  
GNU GPL Version 2. See LICENSE