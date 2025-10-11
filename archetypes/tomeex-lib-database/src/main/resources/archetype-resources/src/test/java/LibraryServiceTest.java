package ${package};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LibraryService
 * 
 * These tests demonstrate how to test business logic independently
 * without any web container or external dependencies.
 */
public class LibraryServiceTest {
    
    private LibraryService libraryService;
    
    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
    }
    
    @Test
    void testGetGreeting() {
        String greeting = libraryService.getGreeting();
        assertNotNull(greeting);
        assertTrue(greeting.contains("${artifactId}"));
        assertTrue(greeting.contains("Library"));
    }
    
    @Test
    void testGetVersion() {
        String version = libraryService.getVersion();
        assertNotNull(version);
        assertEquals("${version}", version);
    }
    
    @Test
    void testProcessDataValid() {
        String result = libraryService.processData("test data");
        assertEquals("${artifactId.toUpperCase()}: TEST DATA", result);
    }
    
    @Test
    void testProcessDataNull() {
        String result = libraryService.processData(null);
        assertEquals("No data provided", result);
    }
    
    @Test
    void testProcessDataEmpty() {
        String result = libraryService.processData("   ");
        assertEquals("No data provided", result);
    }
    
    @Test
    void testProcessDataTrimming() {
        String result = libraryService.processData("  hello world  ");
        assertEquals("${artifactId.toUpperCase()}: HELLO WORLD", result);
    }
    
    @Test
    void testIsValidDataValid() {
        assertTrue(libraryService.isValidData("abc"));
        assertTrue(libraryService.isValidData("hello world"));
        assertTrue(libraryService.isValidData("a".repeat(100)));
    }
    
    @Test
    void testIsValidDataInvalid() {
        assertFalse(libraryService.isValidData(null));
        assertFalse(libraryService.isValidData(""));
        assertFalse(libraryService.isValidData("   "));
        assertFalse(libraryService.isValidData("ab"));
        assertFalse(libraryService.isValidData("a".repeat(101)));
    }
}