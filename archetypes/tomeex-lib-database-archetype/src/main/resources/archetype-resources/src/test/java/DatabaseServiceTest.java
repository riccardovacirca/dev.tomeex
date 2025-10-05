package ${package};

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test template for DatabaseService
 * Add your own test methods here
 */
public class DatabaseServiceTest {
    
    @Test
    void testBasicFunctionality() {
        // Use in-memory H2 for testing
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test" + System.nanoTime());
        DatabaseService service = new DatabaseService(jdbi);
        
        assertNotNull(service.getGreeting());
        assertNotNull(service.getVersion());
    }
}