# Database Class Documentation

**Package:** `dev.tomeex.tools`
**Version:** 1.0.0
**License:** Exclusive Free Beta License

## Overview

The `Database` class provides a simplified abstraction layer for database operations in Java web applications. It integrates seamlessly with JNDI datasources, supports multiple database types (PostgreSQL, MariaDB, SQLite), and offers transaction management with memory-efficient result iteration.

## Key Features

- ✅ JNDI datasource integration
- ✅ Transaction support (begin, commit, rollback)
- ✅ Prepared statement parameter binding
- ✅ Multiple result handling modes (Recordset, Cursor)
- ✅ Auto-detection of last insert ID across database types
- ✅ Zero external dependencies beyond JDBC

---

## Quick Start

### Basic SELECT Query

```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();
    Database.Recordset users = db.select("SELECT * FROM users WHERE status = ?", "active");

    for (Database.Record user : users) {
        String name = (String) user.get("name");
        String email = (String) user.get("email");
        System.out.println(name + " - " + email);
    }
} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

### Basic INSERT/UPDATE/DELETE

```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();

    // INSERT
    int rowsInserted = db.query(
        "INSERT INTO users (name, email, status) VALUES (?, ?, ?)",
        "John Doe", "john@example.com", "active"
    );

    long newId = db.lastInsertId();
    System.out.println("New user ID: " + newId);

    // UPDATE
    int rowsUpdated = db.query(
        "UPDATE users SET status = ? WHERE id = ?",
        "inactive", newId
    );

    // DELETE
    int rowsDeleted = db.query("DELETE FROM users WHERE id = ?", newId);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    db.close();
}
```

---

## Constructor

### `Database(String jndiName)`

Creates a new Database instance configured to use the specified JNDI resource.

**Parameters:**
- `jndiName` - The JNDI resource name (e.g., `"jdbc/MyDB"`)

**⚠️ IMPORTANT:** Do **NOT** include the `"java:comp/env/"` prefix. The class adds this automatically.

**Correct:**
```java
Database db = new Database("jdbc/MyDB");
```

**Incorrect:**
```java
Database db = new Database("java:comp/env/jdbc/MyDB");  // ❌ WRONG
```

---

## Core Methods

### Connection Management

#### `void open() throws Exception`

Opens a database connection using the configured JNDI datasource.

**Throws:** `Exception` if connection fails

**Usage:**
```java
db.open();
```

#### `void close()`

Closes the database connection and releases resources. Safe to call multiple times.

**Usage:**
```java
db.close();  // Always call in finally block
```

#### `boolean connected()`

Checks if the database connection is active and not closed.

**Returns:** `true` if connected, `false` otherwise

**Usage:**
```java
if (db.connected()) {
    // Connection is active
}
```

---

### Query Execution

#### `int query(String sql, Object... params) throws Exception`

Executes modification queries (INSERT, UPDATE, DELETE).

**Parameters:**
- `sql` - SQL statement with `?` placeholders
- `params` - Values to bind to placeholders (varargs)

**Returns:** Number of affected rows

**Throws:** `Exception` if query execution fails

**Usage:**
```java
int affected = db.query("UPDATE users SET active = ? WHERE id = ?", true, 123);
```

#### `Recordset select(String sql, Object... params) throws Exception`

Executes a SELECT query and returns all results in memory.

**Parameters:**
- `sql` - SQL SELECT statement with `?` placeholders
- `params` - Values to bind to placeholders (varargs)

**Returns:** `Recordset` containing all matching records

**Throws:** `Exception` if query execution fails

**Usage:**
```java
Recordset results = db.select("SELECT * FROM users WHERE age > ?", 18);
```

**⚠️ Memory Note:** Loads all results into memory. For large result sets, use `cursor()` instead.

#### `Cursor cursor(String sql, Object... params) throws Exception`

Creates a memory-efficient cursor for iterating large result sets.

**Parameters:**
- `sql` - SQL SELECT statement with `?` placeholders
- `params` - Values to bind to placeholders (varargs)

**Returns:** `Cursor` for result set navigation

**Throws:** `Exception` if cursor creation fails

**Usage:**
```java
Cursor cursor = db.cursor("SELECT * FROM large_table");
try {
    while (cursor.next()) {
        String value = (String) cursor.get("column_name");
        // Process row
    }
} finally {
    cursor.close();
}
```

#### `long lastInsertId() throws Exception`

Returns the auto-generated ID from the last INSERT operation.

**Returns:** Last insert ID as `long`

**Throws:** `Exception` if no ID available or database type unsupported

**Supported Databases:**
- PostgreSQL: Uses `LASTVAL()`
- MariaDB/MySQL: Uses `LAST_INSERT_ID()`
- SQLite: Uses `last_insert_rowid()`

**Usage:**
```java
db.query("INSERT INTO users (name) VALUES (?)", "Alice");
long userId = db.lastInsertId();
```

---

### Transaction Management

#### `void begin() throws Exception`

Starts a database transaction by disabling auto-commit.

**Throws:** `Exception` if connection not available

**Usage:**
```java
db.begin();
```

#### `void commit() throws Exception`

Commits the current transaction and re-enables auto-commit.

**Throws:** `Exception` if connection not available or commit fails

**Usage:**
```java
db.commit();
```

#### `void rollback() throws Exception`

Rolls back the current transaction and re-enables auto-commit.

**Throws:** `Exception` if connection not available or rollback fails

**Usage:**
```java
db.rollback();
```

**Transaction Example:**
```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();
    db.begin();

    db.query("INSERT INTO accounts (name, balance) VALUES (?, ?)", "Alice", 1000);
    db.query("INSERT INTO transactions (description) VALUES (?)", "Initial deposit");

    db.commit();
} catch (Exception e) {
    db.rollback();
    e.printStackTrace();
} finally {
    db.close();
}
```

---

## Data Types

### Record

A single database row represented as a `HashMap<String, Object>`.

**Access Pattern:**
```java
Database.Record record = recordset.get(0);
String name = (String) record.get("name");
Integer age = (Integer) record.get("age");
```

**Available Methods:** All `HashMap` methods (`get()`, `put()`, `containsKey()`, etc.)

### Recordset

A collection of `Record` objects, extends `ArrayList<Record>`.

**Access Pattern:**
```java
Database.Recordset results = db.select("SELECT * FROM users");

// Iterate
for (Database.Record record : results) {
    System.out.println(record.get("name"));
}

// Index access
Database.Record firstRecord = results.get(0);

// Size
int count = results.size();
```

**Available Methods:** All `ArrayList` methods (`size()`, `isEmpty()`, `get()`, etc.)

### Cursor

Memory-efficient iterator for large result sets.

**Methods:**
- `boolean next()` - Move to next row, returns `false` when end reached
- `Object get(String column)` - Get column value from current row
- `Record getRow()` - Get entire current row as Record
- `void close()` - Release resources (always call in finally block)

**Pattern:**
```java
Cursor cursor = db.cursor("SELECT * FROM huge_table");
try {
    while (cursor.next()) {
        Database.Record row = cursor.getRow();
        // Process row
    }
} finally {
    cursor.close();
}
```

---

## JNDI Configuration

### TomEE / Tomcat Context Configuration

Create or edit `src/main/webapp/META-INF/context.xml`:

#### PostgreSQL
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/MyDB"
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="20"
              maxIdle="5"
              maxWaitMillis="10000"
              username="dbuser"
              password="dbpassword"
              driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://localhost:5432/mydatabase"/>
</Context>
```

#### MariaDB / MySQL
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/MyDB"
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="20"
              maxIdle="5"
              maxWaitMillis="10000"
              username="dbuser"
              password="dbpassword"
              driverClassName="org.mariadb.jdbc.Driver"
              url="jdbc:mariadb://localhost:3306/mydatabase"/>
</Context>
```

#### SQLite
```xml
<?xml version="1.0" encoding="UTF-8"?>
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

### Web Application Resource Reference

Add to `src/main/webapp/WEB-INF/web.xml`:

```xml
<resource-ref>
    <description>Database connection</description>
    <res-ref-name>jdbc/MyDB</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

### Required JDBC Drivers

Add appropriate JDBC driver to your `pom.xml`:

**PostgreSQL:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

**MariaDB:**
```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.3.3</version>
</dependency>
```

**SQLite:**
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

---

## Servlet Integration Pattern

### Recommended Pattern (Local Variable)

```java
@WebServlet("/api/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        Database db = null;

        try {
            db = new Database("jdbc/MyDB");
            db.open();

            Database.Recordset users = db.select("SELECT * FROM users");

            // Convert to JSON and write response
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), users);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
```

**⚠️ Important:** Always instantiate `Database` as a local variable in the method, not as a servlet instance field. This ensures proper connection lifecycle management and thread safety.

---

## Best Practices

### ✅ DO

- **Always call `close()`** in a `finally` block
- **Use prepared statement parameters** (`?` placeholders) to prevent SQL injection
- **Instantiate Database as local variable** in servlets/methods
- **Use `cursor()`** for large result sets to save memory
- **Use transactions** for multi-step operations that must succeed or fail together
- **Handle exceptions** appropriately and log errors

### ❌ DON'T

- **Don't store Database instance as servlet field** - creates connection leaks
- **Don't concatenate user input into SQL strings** - SQL injection vulnerability
- **Don't forget to call `close()`** - causes connection pool exhaustion
- **Don't load huge result sets with `select()`** - use `cursor()` instead
- **Don't include "java:comp/env/" in constructor** - added automatically

---

## Common Patterns

### Pattern: Single Record Lookup

```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();
    Database.Recordset results = db.select(
        "SELECT * FROM users WHERE id = ?", userId
    );

    if (!results.isEmpty()) {
        Database.Record user = results.get(0);
        String name = (String) user.get("name");
        return name;
    }
    return null;
} finally {
    db.close();
}
```

### Pattern: Batch Insert with Transaction

```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();
    db.begin();

    for (User user : userList) {
        db.query(
            "INSERT INTO users (name, email) VALUES (?, ?)",
            user.getName(), user.getEmail()
        );
    }

    db.commit();
} catch (Exception e) {
    db.rollback();
    throw e;
} finally {
    db.close();
}
```

### Pattern: Large Result Set Processing

```java
Database db = new Database("jdbc/MyDB");
Cursor cursor = null;
try {
    db.open();
    cursor = db.cursor("SELECT * FROM large_table");

    while (cursor.next()) {
        String value = (String) cursor.get("column_name");
        // Process each row without loading everything into memory
        processRow(value);
    }
} finally {
    if (cursor != null) {
        cursor.close();
    }
    db.close();
}
```

### Pattern: JSON API Response

```java
Database db = new Database("jdbc/MyDB");
try {
    db.open();
    Database.Recordset logs = db.select(
        "SELECT * FROM system_logs ORDER BY created_at DESC LIMIT ?", 100
    );

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(logs);

    return json;
} finally {
    db.close();
}
```

---

## Troubleshooting

### Connection Issues

**Error:** `Name [java:comp/env/jdbc/MyDB] is not bound in this Context`

**Causes:**
1. Context.xml resource not configured
2. JNDI name mismatch between constructor and context.xml
3. Missing web.xml resource-ref declaration
4. Double "java:comp/env/" prefix (common mistake)

**Solution:**
- Verify context.xml has `<Resource name="jdbc/MyDB">`
- Verify constructor uses `new Database("jdbc/MyDB")` (no prefix)
- Verify web.xml has matching `<res-ref-name>jdbc/MyDB</res-ref-name>`

### SQL Errors

**Error:** `user lacks privilege or object not found: TABLE_NAME`

**Causes:**
1. Table doesn't exist
2. Table name case sensitivity (PostgreSQL specific)
3. Wrong database/schema
4. Insufficient permissions

**Solution:**
- For PostgreSQL: Use lowercase table names or quote them: `"TableName"`
- Verify database connection is pointing to correct database
- Check user has SELECT/INSERT/UPDATE/DELETE permissions

### Memory Issues

**Error:** `OutOfMemoryError` with large queries

**Cause:** Using `select()` on huge result sets loads everything into memory

**Solution:** Use `cursor()` instead:
```java
// Instead of:
Recordset huge = db.select("SELECT * FROM million_rows");  // ❌ OOM

// Use:
Cursor cursor = db.cursor("SELECT * FROM million_rows");  // ✅ Memory efficient
```

### Transaction Issues

**Error:** Transaction not rolled back after error

**Cause:** Exception thrown before `rollback()` call

**Solution:** Always call rollback in catch block:
```java
try {
    db.begin();
    // ... operations
    db.commit();
} catch (Exception e) {
    db.rollback();  // ✅ Always rollback on error
    throw e;
}
```

---

## Performance Tips

1. **Connection Pooling:** Configure `maxTotal` and `maxIdle` in context.xml appropriately
2. **Use Prepared Statements:** Always use `?` placeholders (the class does this automatically)
3. **Batch Operations:** Use transactions for multiple inserts/updates
4. **Result Set Size:** Use LIMIT clauses and cursor() for large datasets
5. **Index Your Tables:** Ensure frequently queried columns are indexed
6. **Close Resources:** Always close connections and cursors to return them to pool

---

## Thread Safety

The `Database` class is **NOT thread-safe**. Each thread should create its own instance.

**Safe Pattern (Servlet):**
```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    Database db = new Database("jdbc/MyDB");  // ✅ Thread-local
    // ... use db
}
```

**Unsafe Pattern:**
```java
public class MyServlet extends HttpServlet {
    private Database db = new Database("jdbc/MyDB");  // ❌ Shared across threads
}
```

---

## Version History

**1.0.0** - Initial release
- JNDI datasource support
- Transaction management
- Recordset and Cursor result handling
- Multi-database support (PostgreSQL, MariaDB, SQLite)

---

## License

Copyright (C) 2018-2025 Riccardo Vacirca
Licensed under Exclusive Free Beta License

---

## Support

For issues, questions, or feature requests, contact the TomEEx development team.
