# Database

Java database abstraction layer providing simplified database operations with JNDI integration, transaction support, and cursor-based result iteration.

## Classes

#### Core Classes

[Database](#database-1) - Main database interface for connection management and query execution  
[Database.Record](#databaserecord) - Single database record as key-value map  
[Database.Recordset](#databaserecordset) - Collection of database records  
[Database.Cursor](#databasecursor) - Memory-efficient iterator for large result sets  

## Methods

#### Connection Management

void [open](#open)() throws Exception  
void [close](#close)()  
boolean [connected](#connected)()  

#### Transaction Control

void [begin](#begin)() throws Exception  
void [commit](#commit)() throws Exception  
void [rollback](#rollback)() throws Exception  

#### Query Execution

int [query](#query)(String sql, Object... params) throws Exception  
Recordset [select](#select)(String sql, Object... params) throws Exception  
Cursor [cursor](#cursor)(String sql, Object... params) throws Exception  
long [lastInsertId](#lastinsertid)() throws Exception  

#### Cursor Operations

boolean [Cursor.next](#cursornext)() throws Exception  
Object [Cursor.get](#cursorget)(String column) throws Exception  
Record [Cursor.getRow](#cursorgetrow)() throws Exception  
void [Cursor.close](#cursorclose)()  

# Class Documentation

## Database

`String source` - JNDI resource name for database connection
`Connection connection` - Active database connection instance

Main database interface providing connection management, transaction control, and query execution. Handles JNDI datasource integration and connection pooling automatically.

**Constructor:**
```java
public Database(String jndiName)
```

**Parameters:**
- `jndiName` - JNDI resource name for database connection (e.g., "jdbc/MyDB")

**Key Features:**
- **JNDI Integration** - Seamless integration with application server connection pools
- **Transaction Support** - Complete transaction lifecycle management with rollback capabilities
- **Type-Safe Results** - Structured data types (Record, Recordset) for consistent data handling
- **Cursor Operations** - Memory-efficient result iteration for large datasets
- **Connection Pooling** - Automatic connection pool management through JNDI

**Dependencies:**
- Java 17+
- JNDI-compatible application server (TomEE, etc.)
- Database driver (PostgreSQL, MySQL, SQLite, etc.)

**JNDI Configuration:**

The JNDI resource must be configured in `META-INF/context.xml`. The Database class automatically prepends `java:comp/env/` to the JNDI name during lookup, so you only need to provide the resource name (e.g., "jdbc/MyDB").

PostgreSQL Configuration:
```xml
<Context>
  <Resource name="jdbc/MyDB"
            auth="Container"
            type="javax.sql.DataSource"
            maxTotal="20"
            maxIdle="5"
            maxWaitMillis="10000"
            username="dbuser"
            password="dbpass"
            driverClassName="org.postgresql.Driver"
            url="jdbc:postgresql://localhost:5432/mydb"/>
</Context>
```

MariaDB/MySQL Configuration:
```xml
<Context>
  <Resource name="jdbc/MyDB"
            auth="Container"
            type="javax.sql.DataSource"
            maxTotal="20"
            maxIdle="5"
            maxWaitMillis="10000"
            username="dbuser"
            password="dbpass"
            driverClassName="org.mariadb.jdbc.Driver"
            url="jdbc:mariadb://localhost:3306/mydb"/>
</Context>
```

SQLite Configuration:
```xml
<Context>
  <Resource name="jdbc/MyDB"
            auth="Container"
            type="javax.sql.DataSource"
            maxTotal="20"
            maxIdle="5"
            maxWaitMillis="10000"
            username=""
            password=""
            driverClassName="org.sqlite.JDBC"
            url="jdbc:sqlite:/path/to/database.sqlite"/>
</Context>
```

**JNDI Parameters:**
- `name` - JNDI resource name (must match constructor parameter)
- `auth` - Authentication mode (Container for connection pooling)
- `type` - DataSource type (always javax.sql.DataSource)
- `maxTotal` - Maximum number of connections in pool
- `maxIdle` - Maximum number of idle connections
- `maxWaitMillis` - Maximum wait time for connection (milliseconds)
- `username` - Database username
- `password` - Database password
- `driverClassName` - JDBC driver class name
- `url` - JDBC connection URL

**Example:**
```java
import dev.tomeex.tools.Database;

// Create Database instance with JNDI name
Database db = new Database("jdbc/MyDB");

// The database is now ready to be opened
// JNDI resource "jdbc/MyDB" must be configured in META-INF/context.xml
```

[↑ Classes](#classes)

## Database.Record

```java
public static class Record extends HashMap<String, Object>
```

Represents a single database record as a key-value map. Provides type-safe access to database column values with automatic type conversion.

**Inheritance:**
- Inherits all HashMap methods (get, put, containsKey, etc.)
- Implements Map&lt;String, Object&gt; interface

**Usage:**
```java
Record record = ...;
String name = (String) record.get("name");
Integer age = (Integer) record.get("age");
```

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");
db.open();

// Get a single record
Database.Recordset results = db.select("SELECT * FROM users WHERE id = ?", 1);
if (!results.isEmpty()) {
    Database.Record user = results.get(0);

    // Access record fields
    Long id = (Long) user.get("id");
    String name = (String) user.get("name");
    String email = (String) user.get("email");
    Boolean active = (Boolean) user.get("active");

    System.out.println("User: " + name + " (" + email + ")");
    System.out.println("Active: " + active);

    // Check if field exists
    if (user.containsKey("created_at")) {
        System.out.println("Created: " + user.get("created_at"));
    }
}

db.close();
```

[↑ Classes](#classes)

## Database.Recordset

```java
public static class Recordset extends ArrayList<Record>
```

Collection of database records providing list-like access to query results. Supports standard list operations and iteration.

**Inheritance:**
- Inherits all ArrayList methods (add, remove, size, get, etc.)
- Implements List&lt;Record&gt; interface

**Usage:**
```java
Recordset users = db.select("SELECT * FROM users");
for (Record user : users) {
    System.out.println(user.get("name"));
}
```

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");
db.open();

// Get multiple records
Database.Recordset users = db.select("SELECT * FROM users WHERE active = ?", true);

// Check if results exist
if (users.isEmpty()) {
    System.out.println("No active users found");
} else {
    System.out.println("Found " + users.size() + " active users");

    // Iterate through all records
    for (Database.Record user : users) {
        String name = (String) user.get("name");
        String email = (String) user.get("email");
        System.out.println("- " + name + " <" + email + ">");
    }

    // Access specific record by index
    Database.Record firstUser = users.get(0);
    System.out.println("First user: " + firstUser.get("name"));
}

db.close();
```

[↑ Classes](#classes)

## Database.Cursor

`ResultSet resultSet` - JDBC result set for row iteration
`PreparedStatement statement` - Prepared statement for query execution

Memory-efficient iterator for large result sets. Provides row-by-row access to query results without loading entire dataset into memory.

**Resource Management:**
- Must be closed after use to release database resources
- Implements try-with-resources pattern compatibility

**Methods:**
- `next()` - Moves to next row
- `get(String column)` - Gets specific column value
- `getRow()` - Gets entire row as Record
- `close()` - Closes cursor and releases resources

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");
db.open();

// Create cursor for large result set
Database.Cursor cursor = db.cursor(
    "SELECT id, name, email FROM users WHERE active = ?",
    true
);

try {
    int count = 0;

    // Iterate through results efficiently
    while (cursor.next()) {
        Long id = (Long) cursor.get("id");
        String name = (String) cursor.get("name");
        String email = (String) cursor.get("email");

        System.out.println(id + ": " + name + " <" + email + ">");
        count++;
    }

    System.out.println("Processed " + count + " records");

} finally {
    // Always close cursor to release resources
    cursor.close();
}

db.close();
```

[↑ Classes](#classes)

# Method Documentation

## open

```java
public void open() throws Exception
```

**Description:**
Opens database connection using JNDI datasource lookup. Establishes connection through application server's connection pool.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - JNDI lookup failure, connection establishment failure, or datasource configuration error

**Prerequisites:**
- JNDI resource must be properly configured in application server
- Database driver must be available in classpath

**JNDI Lookup Details:**
The method automatically prepends `java:comp/env/` to the JNDI resource name provided in the constructor. For example, if you create `new Database("jdbc/MyDB")`, the actual JNDI lookup will be performed on `java:comp/env/jdbc/MyDB`

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    // Open connection using JNDI datasource
    db.open();

    System.out.println("Database connection opened successfully");

    // Perform database operations...

} catch (Exception e) {
    System.err.println("Failed to open database: " + e.getMessage());
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## close

```java
public void close()
```

**Description:**
Closes database connection and returns it to connection pool. Safe to call multiple times.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Resource Management:**
- Always call in finally block or use try-with-resources
- Connection returned to pool, not destroyed

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // Perform database operations
    Database.Recordset users = db.select("SELECT * FROM users");
    System.out.println("Found " + users.size() + " users");

} catch (Exception e) {
    e.printStackTrace();
} finally {
    // Always close connection in finally block
    db.close();
    System.out.println("Database connection closed");
}

// Safe to call close() multiple times
db.close(); // No error
```

[↑ Methods](#methods)

## connected

```java
public boolean connected()
```

**Description:**
Checks if database connection is active and valid. Performs actual connection validation.

**Parameters:**
- None

**Return value:**
- `true` - Connection is active and valid
- `false` - No connection or connection is closed/invalid

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

// Check before opening
if (!db.connected()) {
    System.out.println("Not connected yet");
}

try {
    db.open();

    // Verify connection is active
    if (db.connected()) {
        System.out.println("Connection is active");

        // Perform database operations
        Database.Recordset users = db.select("SELECT COUNT(*) as total FROM users");
        System.out.println("Total users: " + users.get(0).get("total"));
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}

// Check after closing
if (!db.connected()) {
    System.out.println("Connection closed");
}
```

[↑ Methods](#methods)

## begin

```java
public void begin() throws Exception
```

**Description:**
Starts database transaction by disabling auto-commit mode. All subsequent operations will be part of the transaction until commit or rollback.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - Connection not available or transaction start failure

**Transaction State:**
- Sets auto-commit to false
- Must be followed by commit() or rollback()

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // Start transaction
    db.begin();
    System.out.println("Transaction started");

    // All subsequent operations are part of this transaction
    db.query("INSERT INTO users (name, email) VALUES (?, ?)",
             "John Doe", "john@example.com");

    db.query("INSERT INTO users (name, email) VALUES (?, ?)",
             "Jane Doe", "jane@example.com");

    // Transaction must be committed or rolled back
    db.commit();

} catch (Exception e) {
    db.rollback();
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## commit

```java
public void commit() throws Exception
```

**Description:**
Commits current transaction and restores auto-commit mode. Makes all transaction changes permanent.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - Connection not available or commit failure

**Transaction State:**
- Commits all pending changes
- Restores auto-commit to true

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();
    db.begin();

    // Insert new user
    db.query("INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
             "Alice Smith", "alice@example.com", true);

    long userId = db.lastInsertId();

    // Update user profile
    db.query("INSERT INTO profiles (user_id, bio) VALUES (?, ?)",
             userId, "Software Developer");

    // Commit all changes permanently
    db.commit();
    System.out.println("Transaction committed successfully");
    System.out.println("Created user ID: " + userId);

} catch (Exception e) {
    System.err.println("Transaction failed, rolling back...");
    db.rollback();
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## rollback

```java
public void rollback() throws Exception
```

**Description:**
Rolls back current transaction and restores auto-commit mode. Discards all transaction changes.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - Connection not available or rollback failure

**Transaction State:**
- Discards all pending changes
- Restores auto-commit to true

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();
    db.begin();

    // Insert user
    db.query("INSERT INTO users (name, email) VALUES (?, ?)",
             "Bob Johnson", "bob@example.com");

    long userId = db.lastInsertId();
    System.out.println("Inserted user ID: " + userId);

    // Simulate an error condition
    if (userId > 0) {
        throw new Exception("Simulated error - rolling back transaction");
    }

    db.commit();

} catch (Exception e) {
    System.err.println("Error occurred: " + e.getMessage());

    // Rollback discards all changes made in this transaction
    db.rollback();
    System.out.println("Transaction rolled back - no changes saved");

} finally {
    db.close();
}
```

[↑ Methods](#methods)

## query

```java
public int query(String sql, Object... params) throws Exception
```

**Description:**
Executes modification queries (INSERT, UPDATE, DELETE) with parameter binding. Uses prepared statements to prevent SQL injection.

**Parameters:**
- `sql` - SQL statement with ? placeholders for parameters
- `params` - Variable arguments for parameter binding (in order)

**Return value:**
- `int` - Number of affected rows

**Exceptions:**
- `Exception` - Connection not available, invalid SQL, or execution failure

**Security:**
- Uses prepared statements for parameter binding
- Prevents SQL injection attacks

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // INSERT example
    int insertedRows = db.query(
        "INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
        "Charlie Brown", "charlie@example.com", true
    );
    System.out.println("Inserted " + insertedRows + " row(s)");

    // UPDATE example
    int updatedRows = db.query(
        "UPDATE users SET active = ? WHERE email = ?",
        false, "charlie@example.com"
    );
    System.out.println("Updated " + updatedRows + " row(s)");

    // DELETE example
    int deletedRows = db.query(
        "DELETE FROM users WHERE email = ?",
        "charlie@example.com"
    );
    System.out.println("Deleted " + deletedRows + " row(s)");

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## select

```java
public Recordset select(String sql, Object... params) throws Exception
```

**Description:**
Executes SELECT queries and returns all results as Recordset. Loads complete result set into memory.

**Parameters:**
- `sql` - SQL SELECT statement with ? placeholders for parameters
- `params` - Variable arguments for parameter binding (in order)

**Return value:**
- `Recordset` - Collection of records containing all query results

**Exceptions:**
- `Exception` - Connection not available, invalid SQL, or execution failure

**Memory Usage:**
- Loads entire result set into memory
- Use cursor() for large result sets

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // Simple SELECT
    Database.Recordset allUsers = db.select("SELECT * FROM users");
    System.out.println("Total users: " + allUsers.size());

    // SELECT with parameters
    Database.Recordset activeUsers = db.select(
        "SELECT id, name, email FROM users WHERE active = ? ORDER BY name",
        true
    );

    System.out.println("\nActive users:");
    for (Database.Record user : activeUsers) {
        System.out.printf("ID: %d, Name: %s, Email: %s%n",
            user.get("id"),
            user.get("name"),
            user.get("email")
        );
    }

    // SELECT with multiple parameters
    Database.Recordset filteredUsers = db.select(
        "SELECT * FROM users WHERE active = ? AND created_at > ?",
        true, "2023-01-01"
    );
    System.out.println("\nRecent active users: " + filteredUsers.size());

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## cursor

```java
public Cursor cursor(String sql, Object... params) throws Exception
```

**Description:**
Creates cursor for memory-efficient iteration over large result sets. Returns iterator that fetches rows on demand.

**Parameters:**
- `sql` - SQL SELECT statement with ? placeholders for parameters
- `params` - Variable arguments for parameter binding (in order)

**Return value:**
- `Cursor` - Iterator for result set navigation

**Exceptions:**
- `Exception` - Connection not available, invalid SQL, or execution failure

**Resource Management:**
- Must call cursor.close() after use
- Use try-with-resources pattern

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // Create cursor for large result set
    Database.Cursor cursor = db.cursor(
        "SELECT id, name, email, created_at FROM users WHERE active = ? ORDER BY id",
        true
    );

    try {
        int processedCount = 0;

        // Process records one at a time
        while (cursor.next()) {
            Long id = (Long) cursor.get("id");
            String name = (String) cursor.get("name");

            // Process record
            System.out.println("Processing user " + id + ": " + name);

            processedCount++;

            // Progress indicator every 100 records
            if (processedCount % 100 == 0) {
                System.out.println("Processed " + processedCount + " records...");
            }
        }

        System.out.println("Total processed: " + processedCount + " records");

    } finally {
        // Always close cursor
        cursor.close();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## lastInsertId

```java
public long lastInsertId() throws Exception
```

**Description:**
Returns auto-generated key from last INSERT operation. Supports multiple database types with appropriate SQL.

**Parameters:**
- None

**Return value:**
- `long` - Auto-generated primary key value from last insert

**Exceptions:**
- `Exception` - Connection not available, unsupported database, or no recent insert

**Database Support:**
- PostgreSQL: LASTVAL()
- MySQL/MariaDB: LAST_INSERT_ID()
- SQLite: last_insert_rowid()

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    // Insert a new user
    int rows = db.query(
        "INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
        "David Wilson", "david@example.com", true
    );

    if (rows > 0) {
        // Get the auto-generated ID
        long newUserId = db.lastInsertId();
        System.out.println("New user created with ID: " + newUserId);

        // Use the ID for related operations
        db.query(
            "INSERT INTO profiles (user_id, bio, avatar) VALUES (?, ?, ?)",
            newUserId, "New user profile", "default.png"
        );

        System.out.println("Profile created for user ID: " + newUserId);
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## Cursor.next

```java
public boolean next() throws Exception
```

**Description:**
Moves cursor to next row in result set. Must be called before accessing row data.

**Parameters:**
- None

**Return value:**
- `true` - Successfully moved to next row, data available
- `false` - No more rows available, end of result set reached

**Exceptions:**
- `Exception` - Cursor operation failure or connection error

**Usage Pattern:**
```java
while (cursor.next()) {
    // Access row data
}
```

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    Database.Cursor cursor = db.cursor(
        "SELECT id, name, status FROM tasks ORDER BY priority DESC"
    );

    try {
        System.out.println("Processing tasks:");

        // next() returns true while rows are available
        while (cursor.next()) {
            Long id = (Long) cursor.get("id");
            String name = (String) cursor.get("name");
            String status = (String) cursor.get("status");

            System.out.println("Task " + id + ": " + name + " [" + status + "]");

            // Process task...
        }

        // next() returns false when no more rows
        System.out.println("All tasks processed");

    } finally {
        cursor.close();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## Cursor.get

```java
public Object get(String column) throws Exception
```

**Description:**
Retrieves value from current cursor row by column name. Returns raw database value.

**Parameters:**
- `column` - Database column name (case-sensitive)

**Return value:**
- `Object` - Column value from current row (may be null)

**Exceptions:**
- `Exception` - Invalid column name, no current row, or access failure

**Type Casting:**
- Cast return value to appropriate type (String, Integer, etc.)
- Handle null values appropriately

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    Database.Cursor cursor = db.cursor(
        "SELECT id, name, email, age, salary, active, created_at FROM users"
    );

    try {
        while (cursor.next()) {
            // Get values by column name
            Long id = (Long) cursor.get("id");
            String name = (String) cursor.get("name");
            String email = (String) cursor.get("email");
            Integer age = (Integer) cursor.get("age");
            Double salary = (Double) cursor.get("salary");
            Boolean active = (Boolean) cursor.get("active");
            java.sql.Timestamp createdAt = (java.sql.Timestamp) cursor.get("created_at");

            // Handle null values
            String ageStr = (age != null) ? age.toString() : "N/A";
            String salaryStr = (salary != null) ? String.format("%.2f", salary) : "N/A";

            System.out.printf("User: %s (%s), Age: %s, Salary: %s, Active: %s%n",
                name, email, ageStr, salaryStr, active);
        }
    } finally {
        cursor.close();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

## Cursor.getRow

```java
public Database.Record getRow() throws Exception
```

**Description:**
Retrieves entire current row as Record object containing all columns. Provides convenient access to complete row data.

**Parameters:**
- None

**Return value:**
- `Database.Record` - Record containing all columns from current row

**Exceptions:**
- `Exception` - No current row, metadata access failure, or row retrieval error

**Usage:**
- Access all columns at once
- Useful for logging or debugging
- Alternative to individual get() calls

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    Database.Cursor cursor = db.cursor(
        "SELECT * FROM users WHERE active = ?",
        true
    );

    try {
        while (cursor.next()) {
            // Get entire row as Record
            Database.Record user = cursor.getRow();

            // Useful for logging complete record
            System.out.println("Full record: " + user);

            // Access fields from the Record
            String name = (String) user.get("name");
            String email = (String) user.get("email");

            // Check if specific fields exist
            if (user.containsKey("phone")) {
                System.out.println("Phone: " + user.get("phone"));
            }

            // Get all column names
            System.out.println("Columns: " + user.keySet());

            // Pass entire record to another method
            processUser(user);
        }
    } finally {
        cursor.close();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}

// Helper method that accepts a Record
private static void processUser(Database.Record user) {
    System.out.println("Processing: " + user.get("name"));
}
```

[↑ Methods](#methods)

## Cursor.close

```java
public void close()
```

**Description:**
Closes cursor and releases associated database resources. Closes underlying ResultSet and PreparedStatement.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Resource Management:**
- Always call after cursor usage
- Safe to call multiple times
- Required to prevent resource leaks

**Example:**
```java
import dev.tomeex.tools.Database;

Database db = new Database("jdbc/MyDB");

try {
    db.open();

    Database.Cursor cursor = db.cursor("SELECT * FROM large_table");

    try {
        int count = 0;
        while (cursor.next() && count < 10) {
            System.out.println("Record: " + cursor.getRow());
            count++;
        }

        // Even if we don't process all rows, close cursor
        System.out.println("Processed " + count + " records, closing cursor");

    } finally {
        // Always close cursor in finally block
        cursor.close();
        System.out.println("Cursor closed, resources released");
    }

    // Safe to call close() multiple times
    cursor.close(); // No error

    // Can create new cursor after closing previous one
    Database.Cursor cursor2 = db.cursor("SELECT * FROM another_table");
    try {
        while (cursor2.next()) {
            // Process...
        }
    } finally {
        cursor2.close();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

[↑ Methods](#methods)

---

@2020-2025 Riccardo Vacirca - All right reserved.
