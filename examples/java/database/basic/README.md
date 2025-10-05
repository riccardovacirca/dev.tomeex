# Java SQL Tutorial - Operazioni Database di Base

Tutorial completo sull'utilizzo delle librerie SQL standard di Java (JDBC) per interagire con database, dalla connessione tramite context.xml all'esecuzione di query complesse con transazioni.

## Indice

- [Configurazione Database](#configurazione-database)
- [Connessione tramite JNDI](#connessione-tramite-jndi)
- [Query Semplici](#query-semplici)
- [Prepared Statements](#prepared-statements)
- [Gestione Transazioni](#gestione-transazioni)
- [Connection Pooling](#connection-pooling)
- [Best Practices](#best-practices)
- [Esempi Completi](#esempi-completi)

## Configurazione Database

### 1. Context.xml - Configurazione DataSource

```xml
<!-- src/main/resources/META-INF/context.xml -->
<Context>
    <Resource 
        name="jdbc/MyDB" 
        auth="Container"
        type="javax.sql.DataSource"
        driverClassName="org.postgresql.Driver"
        url="jdbc:postgresql://localhost:5432/mydb"
        username="myuser"
        password="mypass"
        maxTotal="20"
        maxIdle="5"
        maxWaitMillis="10000"
        removeAbandonedOnMaintenance="true"
        removeAbandonedOnBorrow="true"
        removeAbandonedTimeout="300"
        logAbandoned="true"
        testOnBorrow="true"
        validationQuery="SELECT 1" />
</Context>
```

### 2. Web.xml - Dichiarazione Resource

```xml
<!-- web.xml -->
<resource-ref>
    <description>Database Connection</description>
    <res-ref-name>jdbc/MyDB</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

## Connessione tramite JNDI

### 1. Database Connection Manager

```java
package com.example.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DataSource dataSource;
    
    static {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/MyDB");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup DataSource", e);
        }
    }
    
    /**
     * Ottiene una connessione dal pool
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Chiude connessione in modo sicuro
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test connessione database
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
```

### 2. Connection Utility con Try-With-Resources

```java
package com.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {
    
    /**
     * Esegue operazione con connessione auto-chiusa
     */
    public static <T> T withConnection(ConnectionCallback<T> callback) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            return callback.execute(conn);
        }
    }
    
    /**
     * Esegue operazione in transazione
     */
    public static <T> T withTransaction(ConnectionCallback<T> callback) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = callback.execute(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Chiude Statement in modo sicuro
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Chiude ResultSet in modo sicuro
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
    }
    
    @FunctionalInterface
    public interface ConnectionCallback<T> {
        T execute(Connection conn) throws SQLException;
    }
}
```

## Query Semplici

### 1. SELECT - Lettura Dati

```java
package com.example.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleQueryExample {
    
    /**
     * Seleziona tutti gli utenti
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, created_at FROM users ORDER BY id";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                users.add(user);
            }
        }
        
        return users;
    }
    
    /**
     * Conta il numero di utenti
     */
    public long countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM users";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
        }
    }
    
    /**
     * Verifica esistenza tabella
     */
    public boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
```

### 2. INSERT, UPDATE, DELETE

```java
public class SimpleUpdateExample {
    
    /**
     * Inserisce nuovo utente (NON SICURO - solo per esempio)
     */
    public void insertUserUnsafe(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES ('" 
                    + name + "', '" + email + "', NOW())";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Rows inserted: " + rowsAffected);
        }
    }
    
    /**
     * Aggiorna email utente
     */
    public void updateUserEmail(long userId, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = '" + newEmail + "', updated_at = NOW() WHERE id = " + userId;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Rows updated: " + rowsAffected);
        }
    }
    
    /**
     * Elimina utente
     */
    public void deleteUser(long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = " + userId;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Rows deleted: " + rowsAffected);
        }
    }
}
```

**⚠️ ATTENZIONE**: Gli esempi sopra sono vulnerabili a SQL Injection! Usare sempre Prepared Statements.

## Prepared Statements

### 1. SELECT con Parametri

```java
package com.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PreparedStatementExample {
    
    /**
     * Trova utente per ID
     */
    public Optional<User> findUserById(long id) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }
    
    /**
     * Cerca utenti per email pattern
     */
    public List<User> findUsersByEmailPattern(String pattern) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE email ILIKE ? ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + pattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Utenti creati in range date
     */
    public List<User> findUsersByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE created_at BETWEEN ? AND ? ORDER BY created_at";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
```

### 2. INSERT con Generated Keys

```java
public class PreparedInsertExample {
    
    /**
     * Inserisce utente e ritorna ID generato
     */
    public long insertUser(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            }
            
            throw new SQLException("Failed to insert user, no ID obtained");
        }
    }
    
    /**
     * Batch insert per performance
     */
    public int[] insertMultipleUsers(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (User user : users) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.addBatch();
            }
            
            return stmt.executeBatch();
        }
    }
}
```

### 3. UPDATE e DELETE Sicuri

```java
public class PreparedUpdateExample {
    
    /**
     * Aggiorna profilo utente
     */
    public boolean updateUser(long id, String name, String email) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(4, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Elimina utenti inattivi
     */
    public int deleteInactiveUsers(LocalDateTime cutoffDate) throws SQLException {
        String sql = "DELETE FROM users WHERE last_login < ? OR last_login IS NULL";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(cutoffDate));
            
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Update condizionale con controllo esistenza
     */
    public boolean updateUserIfExists(long id, String name) throws SQLException {
        String sql = "UPDATE users SET name = ?, updated_at = ? WHERE id = ? AND EXISTS (SELECT 1 FROM users WHERE id = ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, id);
            stmt.setLong(4, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
}
```

## Gestione Transazioni

### 1. Transazione Semplice

```java
package com.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionExample {
    
    /**
     * Trasferimento denaro tra account (transazione atomica)
     */
    public void transferMoney(long fromAccountId, long toAccountId, double amount) throws SQLException {
        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // Disabilita auto-commit
            conn.setAutoCommit(false);
            
            try {
                // Addebita dal primo account
                try (PreparedStatement debitStmt = conn.prepareStatement(debitSql)) {
                    debitStmt.setDouble(1, amount);
                    debitStmt.setLong(2, fromAccountId);
                    debitStmt.setDouble(3, amount);
                    
                    int rowsAffected = debitStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient funds or account not found");
                    }
                }
                
                // Accredita al secondo account
                try (PreparedStatement creditStmt = conn.prepareStatement(creditSql)) {
                    creditStmt.setDouble(1, amount);
                    creditStmt.setLong(2, toAccountId);
                    
                    int rowsAffected = creditStmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Destination account not found");
                    }
                }
                
                // Tutto ok, conferma transazione
                conn.commit();
                System.out.println("Transfer completed successfully");
                
            } catch (SQLException e) {
                // Errore, rollback
                conn.rollback();
                System.err.println("Transfer failed: " + e.getMessage());
                throw e;
            } finally {
                // Ripristina auto-commit
                conn.setAutoCommit(true);
            }
        }
    }
}
```

### 2. Transazione Complessa con Savepoints

```java
public class ComplexTransactionExample {
    
    /**
     * Processo ordine complesso con savepoints
     */
    public void processOrder(Order order) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            Savepoint orderSavepoint = null;
            Savepoint inventorySavepoint = null;
            
            try {
                // 1. Crea ordine
                long orderId = insertOrder(conn, order);
                orderSavepoint = conn.setSavepoint("order_created");
                
                // 2. Processa ogni item
                for (OrderItem item : order.getItems()) {
                    try {
                        // Verifica disponibilità
                        if (!checkInventory(conn, item.getProductId(), item.getQuantity())) {
                            throw new SQLException("Insufficient inventory for product " + item.getProductId());
                        }
                        
                        // Savepoint per ogni item
                        inventorySavepoint = conn.setSavepoint("inventory_" + item.getProductId());
                        
                        // Aggiorna inventario
                        updateInventory(conn, item.getProductId(), -item.getQuantity());
                        
                        // Inserisci order item
                        insertOrderItem(conn, orderId, item);
                        
                    } catch (SQLException e) {
                        // Rollback solo questo item se possibile
                        if (inventorySavepoint != null) {
                            conn.rollback(inventorySavepoint);
                            System.err.println("Rolled back item " + item.getProductId() + ": " + e.getMessage());
                        }
                        // Rilancia eccezione per fallire tutto l'ordine
                        throw e;
                    }
                }
                
                // 3. Calcola totale e aggiorna ordine
                double total = calculateOrderTotal(conn, orderId);
                updateOrderTotal(conn, orderId, total);
                
                // 4. Conferma tutto
                conn.commit();
                System.out.println("Order " + orderId + " processed successfully");
                
            } catch (SQLException e) {
                // Rollback completo
                conn.rollback();
                System.err.println("Order processing failed: " + e.getMessage());
                throw e;
            }
        }
    }
    
    private long insertOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, order_date, status) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, order.getCustomerId());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, "PROCESSING");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Failed to create order");
            }
        }
    }
    
    private boolean checkInventory(Connection conn, long productId, int quantity) throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock_quantity") >= quantity;
                }
                return false;
            }
        }
    }
    
    // Altri metodi helper...
}
```

### 3. Transaction Template Pattern

```java
public class TransactionTemplate {
    
    /**
     * Esegue operazione in transazione con template method
     */
    public static <T> T executeInTransaction(TransactionCallback<T> callback) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                T result = callback.doInTransaction(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                } else {
                    throw new SQLException("Transaction failed", e);
                }
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Versione void per operazioni senza return
     */
    public static void executeInTransaction(TransactionVoidCallback callback) throws SQLException {
        executeInTransaction(conn -> {
            callback.doInTransaction(conn);
            return null;
        });
    }
    
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection conn) throws SQLException;
    }
    
    @FunctionalInterface
    public interface TransactionVoidCallback {
        void doInTransaction(Connection conn) throws SQLException;
    }
}

// Uso del template:
public void businessOperation() throws SQLException {
    TransactionTemplate.executeInTransaction(conn -> {
        // Tutte le operazioni qui sono in transazione
        insertUser(conn, "John", "john@example.com");
        updateUserStatus(conn, userId, "ACTIVE");
        logUserActivity(conn, userId, "CREATED");
        return null;
    });
}
```

## Connection Pooling

### 1. Configurazione Pool Avanzata

```xml
<!-- Context.xml con pool ottimizzato -->
<Context>
    <Resource 
        name="jdbc/MyDB" 
        auth="Container"
        type="javax.sql.DataSource"
        driverClassName="org.postgresql.Driver"
        url="jdbc:postgresql://localhost:5432/mydb"
        username="myuser"
        password="mypass"
        
        initialSize="5"
        maxTotal="50"
        maxIdle="20"
        minIdle="5"
        maxWaitMillis="30000"
        
        testOnBorrow="true"
        testOnReturn="false"
        testWhileIdle="true"
        validationQuery="SELECT 1"
        validationQueryTimeout="30"
        timeBetweenEvictionRunsMillis="30000"
        minEvictableIdleTimeMillis="300000"
        
        removeAbandonedOnMaintenance="true"
        removeAbandonedOnBorrow="true"
        removeAbandonedTimeout="300"
        logAbandoned="true"
        
        defaultAutoCommit="true"
        defaultReadOnly="false"
        defaultTransactionIsolation="READ_COMMITTED" />
</Context>
```

### 2. Pool Monitoring

```java
public class PoolMonitor {
    
    /**
     * Statistiche del pool di connessioni
     */
    public void printPoolStats() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MyDB");
            
            // Se è Apache Commons DBCP
            if (ds instanceof org.apache.commons.dbcp2.BasicDataSource) {
                org.apache.commons.dbcp2.BasicDataSource basicDS = 
                    (org.apache.commons.dbcp2.BasicDataSource) ds;
                
                System.out.println("=== Connection Pool Stats ===");
                System.out.println("Active connections: " + basicDS.getNumActive());
                System.out.println("Idle connections: " + basicDS.getNumIdle());
                System.out.println("Max total: " + basicDS.getMaxTotal());
                System.out.println("Max idle: " + basicDS.getMaxIdle());
                System.out.println("Min idle: " + basicDS.getMinIdle());
            }
            
        } catch (NamingException e) {
            throw new SQLException("Failed to lookup DataSource", e);
        }
    }
    
    /**
     * Test del pool sotto carico
     */
    public void stressTestPool() throws SQLException {
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        try (Connection conn = DatabaseManager.getConnection();
                             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
                             ResultSet rs = stmt.executeQuery()) {
                            
                            if (rs.next()) {
                                System.out.println("Thread " + Thread.currentThread().getName() + 
                                                 " - Count: " + rs.getInt(1));
                            }
                        }
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
    }
}
```

## Best Practices

### 1. Resource Management

```java
public class BestPracticesExample {
    
    /**
     * ✅ CORRETTO: Try-with-resources
     */
    public List<User> getUsersCorrect() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        // Tutte le risorse vengono chiuse automaticamente
        
        return users;
    }
    
    /**
     * ❌ SBAGLIATO: Resource leak
     */
    public List<User> getUsersWrong() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection(); // Non chiusa!
        PreparedStatement stmt = conn.prepareStatement("SELECT id, name, email FROM users"); // Non chiuso!
        ResultSet rs = stmt.executeQuery(); // Non chiuso!
        
        while (rs.next()) {
            users.add(mapUser(rs));
        }
        
        return users; // Memory leak!
    }
    
    /**
     * ✅ CORRETTO: Gestione eccezioni con cleanup
     */
    public void updateUserSafe(long id, String name) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.prepareStatement("UPDATE users SET name = ? WHERE id = ?");
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } finally {
            DatabaseUtils.closeStatement(stmt);
            DatabaseUtils.closeConnection(conn);
        }
    }
}
```

### 2. Error Handling

```java
public class ErrorHandlingExample {
    
    /**
     * Gestione errori specifica per tipo
     */
    public void handleSQLErrors() {
        try {
            // Operazione database
            insertUser("John", "john@example.com");
            
        } catch (SQLException e) {
            // Codici errore PostgreSQL
            String sqlState = e.getSQLState();
            int errorCode = e.getErrorCode();
            
            switch (sqlState) {
                case "23505": // Unique violation
                    System.err.println("User already exists: " + e.getMessage());
                    break;
                case "23503": // Foreign key violation
                    System.err.println("Referenced record not found: " + e.getMessage());
                    break;
                case "23514": // Check constraint violation
                    System.err.println("Data validation failed: " + e.getMessage());
                    break;
                case "08001": // Connection error
                    System.err.println("Database connection failed: " + e.getMessage());
                    break;
                default:
                    System.err.println("Database error [" + sqlState + ":" + errorCode + "]: " + e.getMessage());
            }
        }
    }
    
    /**
     * Retry logic per errori temporanei
     */
    public boolean insertUserWithRetry(String name, String email) {
        int maxRetries = 3;
        int retryDelay = 1000; // 1 secondo
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                insertUser(name, email);
                return true; // Successo
                
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                
                // Retry solo per errori temporanei
                if ("08001".equals(sqlState) || "08006".equals(sqlState)) { // Connection errors
                    System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                    
                    if (attempt < maxRetries) {
                        try {
                            Thread.sleep(retryDelay * attempt); // Backoff progressivo
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return false;
                        }
                        continue;
                    }
                }
                
                // Non retry per altri errori
                System.err.println("Operation failed permanently: " + e.getMessage());
                return false;
            }
        }
        
        return false;
    }
}
```

## Esempi Completi

### 1. User Repository Completo

```java
package com.example.repository;

import com.example.db.DatabaseUtils;
import com.example.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    
    // CREATE
    public long createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?) RETURNING id";
        
        return DatabaseUtils.withConnection(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("id");
                    }
                    throw new SQLException("Failed to create user");
                }
            }
        });
    }
    
    // READ
    public Optional<User> findById(long id) throws SQLException {
        String sql = "SELECT id, name, email, created_at, updated_at FROM users WHERE id = ?";
        
        return DatabaseUtils.withConnection(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapUser(rs));
                    }
                    return Optional.empty();
                }
            }
        });
    }
    
    public List<User> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT id, name, email, created_at, updated_at FROM users ORDER BY id LIMIT ? OFFSET ?";
        
        return DatabaseUtils.withConnection(conn -> {
            List<User> users = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, limit);
                stmt.setInt(2, offset);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        users.add(mapUser(rs));
                    }
                }
            }
            return users;
        });
    }
    
    // UPDATE
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, updated_at = ? WHERE id = ?";
        
        return DatabaseUtils.withConnection(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setLong(4, user.getId());
                
                return stmt.executeUpdate() > 0;
            }
        });
    }
    
    // DELETE
    public boolean deleteUser(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        return DatabaseUtils.withConnection(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                return stmt.executeUpdate() > 0;
            }
        });
    }
    
    // SEARCH
    public List<User> searchByEmail(String emailPattern) throws SQLException {
        String sql = "SELECT id, name, email, created_at, updated_at FROM users WHERE email ILIKE ? ORDER BY name";
        
        return DatabaseUtils.withConnection(conn -> {
            List<User> users = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + emailPattern + "%");
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        users.add(mapUser(rs));
                    }
                }
            }
            return users;
        });
    }
    
    // COUNT
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM users";
        
        return DatabaseUtils.withConnection(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
                return 0L;
            }
        });
    }
    
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}
```

### 2. Servlet con Database Integration

```java
package com.example.servlet;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private UserRepository userRepository;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        this.userRepository = new UserRepository();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/users - List all users
                handleGetAllUsers(req, resp);
            } else {
                // GET /api/users/{id} - Get user by ID
                handleGetUserById(pathInfo, resp);
            }
        } catch (SQLException e) {
            handleDatabaseError(resp, e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            User user = objectMapper.readValue(req.getReader(), User.class);
            long userId = userRepository.createUser(user);
            
            Optional<User> createdUser = userRepository.findById(userId);
            if (createdUser.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendJsonResponse(resp, createdUser.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            
        } catch (SQLException e) {
            handleDatabaseError(resp, e);
        }
    }
    
    private void handleGetAllUsers(HttpServletRequest req, HttpServletResponse resp) 
            throws SQLException, IOException {
        
        int limit = getIntParameter(req, "limit", 20);
        int offset = getIntParameter(req, "offset", 0);
        String search = req.getParameter("search");
        
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.searchByEmail(search);
        } else {
            users = userRepository.findAll(limit, offset);
        }
        
        sendJsonResponse(resp, users);
    }
    
    private void handleGetUserById(String pathInfo, HttpServletResponse resp) 
            throws SQLException, IOException {
        
        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            Optional<User> user = userRepository.findById(userId);
            
            if (user.isPresent()) {
                sendJsonResponse(resp, user.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleDatabaseError(HttpServletResponse resp, SQLException e) throws IOException {
        System.err.println("Database error: " + e.getMessage());
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write("{\"error\":\"Database operation failed\"}");
    }
    
    private void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), data);
    }
    
    private int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        String value = req.getParameter(name);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
```

---

Questo tutorial fornisce una base solida per lavorare con SQL in Java, dalla configurazione base alle operazioni complesse con transazioni. Tutti gli esempi seguono le best practice moderne e sono pronti per essere usati in applicazioni production.