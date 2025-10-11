package ${package};

import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;

/**
 * ${artifactId} database service class
 *
 * Add your database operations and business logic here.
 * Uses JDBI for database access.
 *
 * Supports PostgreSQL, MariaDB, and SQLite
 */
public class DatabaseService {

    private final Jdbi jdbi;
    private final String version = "${version}";

    /**
     * Constructor with database connection
     * @param jdbi JDBI instance for database access
     */
    public DatabaseService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Convenience constructor with JDBC URL
     * @param jdbcUrl JDBC connection URL
     */
    public DatabaseService(String jdbcUrl) {
        this.jdbi = DatabaseFactory.create(jdbcUrl);
    }

    /**
     * Convenience constructor with credentials
     * @param jdbcUrl JDBC connection URL
     * @param username database username
     * @param password database password
     */
    public DatabaseService(String jdbcUrl, String username, String password) {
        this.jdbi = DatabaseFactory.create(jdbcUrl, username, password);
    }
    
    /**
     * Returns a greeting message
     * @return greeting string
     */
    public String getGreeting() {
        return "Hello from ${artifactId} Database Library!";
    }
    
    /**
     * Returns the library version
     * @return version string
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Example database operation
     * Add your database methods here
     */
    public boolean testConnection() {
        try {
            return jdbi.withHandle(handle -> {
                Integer result = handle.createQuery("SELECT 1")
                    .mapTo(Integer.class)
                    .one();
                return result != null;
            });
        } catch (Exception e) {
            return false;
        }
    }
    
    // Add your database operations here
}