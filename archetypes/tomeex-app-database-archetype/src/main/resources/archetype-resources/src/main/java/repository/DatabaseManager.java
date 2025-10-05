package ${package}.repository;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Jdbi jdbi;
    
    private DatabaseManager() {
        try {
            // Get DataSource from TomEE JNDI
            Context initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/${artifactId}DB");
            
            // Configure JDBI
            this.jdbi = Jdbi.create(ds)
                           .installPlugin(new SqlObjectPlugin());
                           
        } catch (NamingException e) {
            throw new RuntimeException("Failed to setup database connection", e);
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Jdbi getJdbi() {
        return jdbi;
    }
}