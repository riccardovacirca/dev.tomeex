package ${package};

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

/**
 * Factory for creating database connections supporting PostgreSQL, MariaDB, and SQLite
 */
public class DatabaseFactory {

    /**
     * Create JDBI instance from JDBC URL
     * Automatically detects database type and configures appropriately
     *
     * @param jdbcUrl JDBC connection URL
     * @return configured Jdbi instance
     */
    public static Jdbi create(String jdbcUrl) {
        Jdbi jdbi = Jdbi.create(jdbcUrl);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }

    /**
     * Create JDBI instance with username and password
     *
     * @param jdbcUrl JDBC connection URL
     * @param username database username
     * @param password database password
     * @return configured Jdbi instance
     */
    public static Jdbi create(String jdbcUrl, String username, String password) {
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }

    /**
     * Detect database type from JDBC URL
     *
     * @param jdbcUrl JDBC connection URL
     * @return database type (postgres, mariadb, sqlite)
     */
    public static String detectDatabaseType(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return "postgres";
        } else if (jdbcUrl.startsWith("jdbc:mariadb:") || jdbcUrl.startsWith("jdbc:mysql:")) {
            return "mariadb";
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return "sqlite";
        }
        return "unknown";
    }
}
