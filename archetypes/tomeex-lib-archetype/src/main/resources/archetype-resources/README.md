# ${artifactId} Library

${description}

## Quick Start

### Building the Library

```bash
# Build JAR library
make build

# Run unit tests
make test

# Install to local Maven repository
make install
```

### Using in Other Projects

After installing the library locally, add this dependency to any webapp or application:

```xml
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>
```

### Example Usage

```java
import ${package}.LibraryService;

public class MyApplication {
    public static void main(String[] args) {
        LibraryService service = new LibraryService();
        
        System.out.println(service.getGreeting());
        System.out.println("Version: " + service.getVersion());
        
        String result = service.processData("hello world");
        System.out.println("Processed: " + result);
    }
}
```

## Architecture

This library follows best practices for reusable Java libraries:

- **Pure Business Logic**: No external dependencies (except JUnit for testing)
- **Framework Agnostic**: Can be used in webapps, CLI applications, batch jobs
- **Well Tested**: Comprehensive unit test suite
- **Standard Maven Structure**: Easy to integrate and build

## Development

### Available Make Targets

```bash
make help              # Show all available targets
make build             # Build JAR library
make test              # Run unit tests
make install           # Install to local Maven repository
make clean             # Clean build artifacts
make docs              # Generate Javadoc

make dev-build         # Full development build
make dev-install       # Clean install to local repository
make check-repo        # Check if library is in local Maven repo
make coordinates       # Show Maven dependency coordinates
```

### Project Structure

```
${artifactId}/
├── pom.xml                    # Maven configuration
├── Makefile                   # Build automation
├── README.md                  # This documentation
├── src/main/java/             # Library source code
│   └── ${package}/
│       └── LibraryService.java    # Main service class
└── src/test/java/             # Unit tests
    └── ${package}/
        └── LibraryServiceTest.java # Test suite
```

### Testing Strategy

The library includes comprehensive unit tests that:

- Test all public methods
- Cover edge cases (null, empty, invalid inputs)
- Validate business logic without external dependencies
- Run fast and can be executed in any environment

### Reusability

This library can be:

1. **Imported by webapps**: Add as dependency in WAR projects
2. **Used in CLI applications**: Build standalone applications
3. **Integrated in batch jobs**: Use for scheduled processing
4. **Extended**: Inherit or compose for specialized functionality

### Best Practices

- **Keep dependencies minimal**: Only add what's absolutely necessary
- **Write comprehensive tests**: Ensure reliability across different usage contexts
- **Document public APIs**: Use Javadoc for all public methods
- **Version carefully**: Use semantic versioning for compatibility

## Deployment

### Local Development

```bash
# Install locally for use in other projects
make install

# Verify installation
make check-repo
```

### Integration with Webapps

After installing locally, any webapp in this TomEE environment can use the library:

1. Add dependency to webapp's `pom.xml`
2. Import classes in servlet code
3. Maven will automatically include the JAR in WAR file
4. Deploy webapp normally - library will be in `WEB-INF/lib/`

---

Generated with tomee-jar-library archetype  
Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
License: GNU GPL Version 2.