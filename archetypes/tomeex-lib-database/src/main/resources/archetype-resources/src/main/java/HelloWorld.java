package ${package};

import dev.tomeex.tools.Database;

/**
 * HelloWorld example class demonstrating dev.tomeex.tools.Database usage
 *
 * This class shows how to use the Database utility to execute SQL queries
 * and retrieve results from PostgreSQL, MariaDB, or SQLite databases.
 *
 * It includes proper error handling for connection failures.
 */
public class HelloWorld {

    private final String jndiName;

    /**
     * Constructor with JNDI resource name
     * @param jndiName JNDI resource name (e.g., "jdbc/MainDB")
     */
    public HelloWorld(String jndiName) {
        this.jndiName = jndiName;
    }

    /**
     * Executes a simple SELECT query that returns "Hello, World!"
     * This demonstrates basic query execution using dev.tomeex.tools.Database
     *
     * @return the greeting message from database
     * @throws Exception if database query fails
     */
    public String getGreetingFromDatabase() throws Exception {
        Database db = new Database(jndiName);

        try {
            db.open();

            // Execute a simple SELECT that returns a string
            Database.Recordset results = db.select("SELECT 'Hello, World!' AS message");

            if (results != null && !results.isEmpty()) {
                Database.Record firstRow = results.get(0);
                Object message = firstRow.get("message");
                return message != null ? message.toString() : "No message";
            }

            return "No results";

        } finally {
            db.close();
        }
    }

    /**
     * Prints the greeting message retrieved from database to standard output
     * @throws Exception if database query fails
     */
    public void printGreeting() throws Exception {
        String greeting = getGreetingFromDatabase();
        System.out.println(greeting);
    }

    /**
     * Tests database server connectivity with detailed error handling
     *
     * @return ConnectionResult object with status and message
     */
    public ConnectionResult testConnection() {
        Database db = new Database(jndiName);

        try {
            db.open();

            // If we get here, connection succeeded - now try a query
            try {
                Database.Recordset results = db.select("SELECT 1 AS test");

                if (results != null && !results.isEmpty()) {
                    return new ConnectionResult(true, "Connection successful", null);
                } else {
                    return new ConnectionResult(false, "Query returned no results", null);
                }

            } catch (Exception queryError) {
                // Connection succeeded but query failed - return server's error message
                String errorMsg = queryError.getMessage() != null ? queryError.getMessage() : queryError.toString();
                return new ConnectionResult(false, errorMsg, queryError);
            }

        } catch (Exception connectionError) {
            // Connection failed - server not reachable
            return new ConnectionResult(false, "Server not reachable", connectionError);

        } finally {
            db.close();
        }
    }

    /**
     * Checks if database server is reachable (simple boolean check)
     * @return true if connection successful, false otherwise
     */
    public boolean isServerReachable() {
        return testConnection().isSuccess();
    }

    /**
     * Gets the JNDI resource name being used
     * @return JNDI resource name
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * Result object for connection test
     */
    public static class ConnectionResult {
        private final boolean success;
        private final String message;
        private final Exception exception;

        public ConnectionResult(boolean success, String message, Exception exception) {
            this.success = success;
            this.message = message;
            this.exception = exception;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Exception getException() {
            return exception;
        }

        @Override
        public String toString() {
            return success ? "✓ " + message : "✗ " + message;
        }
    }
}
