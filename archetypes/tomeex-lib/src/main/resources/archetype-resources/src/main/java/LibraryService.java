package ${package};

/**
 * ${artifactId} main service class
 * 
 * Add your business logic methods here.
 * This library can be imported by webapps, CLI applications, etc.
 */
public class LibraryService {
    
    private final String version = "${version}";
    
    /**
     * Returns a greeting message
     */
    public String getGreeting() {
        return "Hello from ${artifactId} Library!";
    }
    
    /**
     * Returns the library version
     */
    public String getVersion() {
        return version;
    }
    
    // Add your business logic methods here
}