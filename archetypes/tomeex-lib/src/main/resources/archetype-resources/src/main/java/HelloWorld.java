package ${package};

/**
 * HelloWorld example class
 *
 * This class demonstrates a simple library with basic functionality.
 * It can be imported and used by any Java application (webapp, CLI, batch jobs, etc.)
 */
public class HelloWorld {

    private final String version = "${version}";

    /**
     * Returns a greeting message
     * @return greeting string
     */
    public String getGreeting() {
        return "Hello, World!";
    }

    /**
     * Returns a personalized greeting
     * @param name the name to greet
     * @return personalized greeting string
     */
    public String getGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getGreeting();
        }
        return "Hello, " + name.trim() + "!";
    }

    /**
     * Prints the greeting message to standard output
     */
    public void printGreeting() {
        System.out.println(getGreeting());
    }

    /**
     * Prints a personalized greeting to standard output
     * @param name the name to greet
     */
    public void printGreeting(String name) {
        System.out.println(getGreeting(name));
    }

    /**
     * Returns the library version
     * @return version string
     */
    public String getVersion() {
        return version;
    }
}
