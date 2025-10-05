package ${package};

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test template for LibraryService
 * Add your own test methods here
 */
public class LibraryServiceTest {
    
    @Test
    void testBasicFunctionality() {
        LibraryService service = new LibraryService();
        
        assertNotNull(service.getGreeting());
        assertNotNull(service.getVersion());
    }
}