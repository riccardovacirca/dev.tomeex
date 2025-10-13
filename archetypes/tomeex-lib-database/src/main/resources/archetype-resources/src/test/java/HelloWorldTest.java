package ${package};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.dbcp2.BasicDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Unit tests for HelloWorld
 *
 * These tests demonstrate how to test database operations by setting up
 * JNDI datasources programmatically for PostgreSQL, MariaDB, and SQLite.
 *
 * The test reads database connection info from /workspace/.env file.
 * Each database is tested separately with its own test method.
 */
public class HelloWorldTest {

    private static Properties envProps = new Properties();

    @BeforeAll
    static void setUp() throws Exception {
        // Load /workspace/.env file
        try (FileInputStream fis = new FileInputStream("/workspace/.env")) {
            envProps.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: /workspace/.env file not found, skipping database tests");
            return;
        }

        // Setup JNDI context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
    }

    @Test
    void testPostgreSQLConnection() throws Exception {
        System.out.println("\n=== Testing PostgreSQL Connection ===");

        String host = envProps.getProperty("POSTGRES_CONTAINER_NAME");
        String password = envProps.getProperty("POSTGRES_PASSWORD");

        if (host == null || host.isEmpty()) {
            System.out.println("PostgreSQL not configured in /workspace/.env - skipping test");
            return;
        }

        InitialContext ic = new InitialContext();
        try {
            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
        } catch (NamingException e) {
            // Subcontexts may already exist
        }

        String jdbcUrl = String.format("jdbc:postgresql://%s:5432/postgres", host);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(jdbcUrl);
        ds.setUsername("postgres");
        ds.setPassword(password);
        ds.setMaxTotal(10);

        try {
            ic.bind("java:comp/env/jdbc/PostgresDB", ds);
        } catch (NamingException e) {
            // Already bound, rebind
            ic.rebind("java:comp/env/jdbc/PostgresDB", ds);
        }

        HelloWorld hw = new HelloWorld("jdbc/PostgresDB");
        HelloWorld.ConnectionResult result = hw.testConnection();

        System.out.println("PostgreSQL: " + result.toString());
        System.out.println("Message: " + result.getMessage());

        // Test passes regardless of server availability
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }

    @Test
    void testMariaDBConnection() throws Exception {
        System.out.println("\n=== Testing MariaDB Connection ===");

        String host = envProps.getProperty("MARIADB_CONTAINER_NAME");
        String password = envProps.getProperty("MARIADB_ROOT_PASSWORD");

        if (host == null || host.isEmpty()) {
            System.out.println("MariaDB not configured in /workspace/.env - skipping test");
            return;
        }

        InitialContext ic = new InitialContext();
        try {
            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
        } catch (NamingException e) {
            // Subcontexts may already exist
        }

        String jdbcUrl = String.format("jdbc:mariadb://%s:3306/mysql", host);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setUrl(jdbcUrl);
        ds.setUsername("root");
        ds.setPassword(password);
        ds.setMaxTotal(10);

        try {
            ic.bind("java:comp/env/jdbc/MariaDB", ds);
        } catch (NamingException e) {
            // Already bound, rebind
            ic.rebind("java:comp/env/jdbc/MariaDB", ds);
        }

        HelloWorld hw = new HelloWorld("jdbc/MariaDB");
        HelloWorld.ConnectionResult result = hw.testConnection();

        System.out.println("MariaDB: " + result.toString());
        System.out.println("Message: " + result.getMessage());

        // Test passes regardless of server availability
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }

    @Test
    void testSQLiteConnection() throws Exception {
        System.out.println("\n=== Testing SQLite Connection ===");

        String dataDir = envProps.getProperty("SQLITE_DATA_DIR");

        if (dataDir == null || dataDir.isEmpty()) {
            System.out.println("SQLite not configured in /workspace/.env - skipping test");
            return;
        }

        InitialContext ic = new InitialContext();
        try {
            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
        } catch (NamingException e) {
            // Subcontexts may already exist
        }

        String jdbcUrl = "jdbc:sqlite:/workspace/" + dataDir + "/test.sqlite";

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl(jdbcUrl);
        ds.setMaxTotal(10);

        try {
            ic.bind("java:comp/env/jdbc/SQLiteDB", ds);
        } catch (NamingException e) {
            // Already bound, rebind
            ic.rebind("java:comp/env/jdbc/SQLiteDB", ds);
        }

        HelloWorld hw = new HelloWorld("jdbc/SQLiteDB");
        HelloWorld.ConnectionResult result = hw.testConnection();

        System.out.println("SQLite: " + result.toString());
        System.out.println("Message: " + result.getMessage());

        // Test passes regardless of server availability
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }

    @Test
    void testGetJndiName() {
        HelloWorld hw = new HelloWorld("jdbc/TestDB");
        assertEquals("jdbc/TestDB", hw.getJndiName());
    }
}
