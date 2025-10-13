package ${package};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HelloWorld
 *
 * These tests demonstrate how to test library functionality
 * without any external dependencies or web container.
 */
public class HelloWorldTest {

    private HelloWorld helloWorld;

    @BeforeEach
    void setUp() {
        helloWorld = new HelloWorld();
    }

    @Test
    void testGetGreeting() {
        String greeting = helloWorld.getGreeting();
        assertNotNull(greeting);
        assertEquals("Hello, World!", greeting);
    }

    @Test
    void testGetGreetingWithName() {
        String greeting = helloWorld.getGreeting("Alice");
        assertNotNull(greeting);
        assertEquals("Hello, Alice!", greeting);
    }

    @Test
    void testGetGreetingWithNull() {
        String greeting = helloWorld.getGreeting(null);
        assertNotNull(greeting);
        assertEquals("Hello, World!", greeting);
    }

    @Test
    void testGetGreetingWithEmptyString() {
        String greeting = helloWorld.getGreeting("");
        assertNotNull(greeting);
        assertEquals("Hello, World!", greeting);
    }

    @Test
    void testGetGreetingWithWhitespace() {
        String greeting = helloWorld.getGreeting("   ");
        assertNotNull(greeting);
        assertEquals("Hello, World!", greeting);
    }

    @Test
    void testGetGreetingTrimsName() {
        String greeting = helloWorld.getGreeting("  Bob  ");
        assertNotNull(greeting);
        assertEquals("Hello, Bob!", greeting);
    }

    @Test
    void testPrintGreeting() {
        assertDoesNotThrow(() -> helloWorld.printGreeting());
    }

    @Test
    void testPrintGreetingWithName() {
        assertDoesNotThrow(() -> helloWorld.printGreeting("Charlie"));
    }

    @Test
    void testGetVersion() {
        String version = helloWorld.getVersion();
        assertNotNull(version);
        assertEquals("${version}", version);
    }
}
