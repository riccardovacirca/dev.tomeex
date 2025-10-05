# Microservice Architecture Tutorial

## Table of Contents

- [Quick Start](#quick-start)
- [Architecture Overview](#architecture-overview)
- [Library Module](#library-module)
- [Webapp Module](#webapp-module)
- [Maven Configuration](#maven-configuration)
- [Build Process](#build-process)
- [Deployment](#deployment)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)

## Quick Start

### API Endpoint

**GET /api/service**

Returns a service response with business logic from JAR library.

**Response:**
```json
{
  "message": "Hello from Microservice!",
  "service": "Microservice Architecture Example",
  "version": "Microservice Library v1.0.0",
  "servlet": "com.example.webapp.MicroserviceServlet",
  "status": "success"
}
```

### Quick Build Commands

```bash
# Build both JAR and WAR
make build

# Deploy to TomEE
make deploy

# Check status
make status

# Clean build artifacts
make clean
```

### Testing the API

```bash
# Test service endpoint
curl http://localhost:9292/microservice-webapp/api/service

# Test with curl verbose output
curl -v http://localhost:9292/microservice-webapp/api/service
```

Perfect for learning microservice architecture and JAR+WAR integration!

[↑ Back to Contents](#table-of-contents)

## Architecture Overview

### Microservice Components

The project demonstrates a complete microservice architecture with:

1. **Business Logic Layer (JAR)**: Pure Java library with no web dependencies
2. **Web Interface Layer (WAR)**: Servlet-based web application that consumes the JAR
3. **Separation of Concerns**: Clear division between business logic and web presentation

### Benefits of This Architecture

- **Reusability**: JAR library can be used by multiple applications
- **Testability**: Business logic can be tested independently
- **Maintainability**: Changes to web layer don't affect business logic
- **Scalability**: Components can be developed and deployed separately

[↑ Back to Contents](#table-of-contents)

## Library Module

### MicroserviceLib Class

```java
package com.example.lib;

public class MicroserviceLib {

    public String getGreeting() {
        return "Hello from Microservice!";
    }

    public String getVersion() {
        return "Microservice Library v1.0.0";
    }

    public String getServiceInfo() {
        return "Microservice Architecture Example";
    }
}
```

Pure business logic with no web dependencies. Can be tested independently and reused across applications.

### Library POM Configuration

```xml
<groupId>com.example</groupId>
<artifactId>microservice-lib</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
```

Standard JAR packaging with minimal dependencies for maximum reusability.

[↑ Back to Contents](#table-of-contents)

## Webapp Module

### MicroserviceServlet Class

```java
@WebServlet("/api/service")
public class MicroserviceServlet extends HttpServlet {
    
    private final MicroserviceLib microserviceLib;
    
    public MicroserviceServlet() {
        this.microserviceLib = new MicroserviceLib();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String message = microserviceLib.getGreeting();
        
        // Generate HTML response with service information
    }
}
```

Web layer that consumes the JAR library and provides HTTP interface.

### Key Integration Points

- **JAR Dependency**: WAR module depends on JAR module in Maven POM
- **Automatic Packaging**: Maven automatically includes JAR in WEB-INF/lib/
- **Runtime Loading**: Servlet container loads JAR classes at runtime
- **Separation**: Web concerns (HTTP, HTML) separate from business logic

[↑ Back to Contents](#table-of-contents)

## Maven Configuration

### Parent POM Structure

```xml
<artifactId>microservice</artifactId>
<packaging>pom</packaging>

<modules>
    <module>microservice-lib</module>
    <module>microservice-webapp</module>
</modules>
```

Multi-module Maven project ensures correct build order: JAR first, then WAR.

### JAR Module POM

```xml
<artifactId>microservice-lib</artifactId>
<packaging>jar</packaging>

<dependencies>
    <!-- No external dependencies - pure Java -->
</dependencies>
```

Minimal dependencies for maximum portability and reusability.

### WAR Module POM

```xml
<artifactId>microservice-webapp</artifactId>
<packaging>war</packaging>

<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>microservice-lib</artifactId>
        <version>1.0.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

WAR depends on JAR with compile scope, ensuring JAR is included in final WAR package.

[↑ Back to Contents](#table-of-contents)

## Build Process

### Maven Build Order

```bash
# Maven builds in dependency order automatically
mvn package

# Explicit order:
# 1. microservice-lib (JAR)
# 2. microservice-webapp (WAR)
```

Maven respects module dependencies and builds JAR before WAR.

### Build Artifacts

```
microservice-lib/target/microservice-lib.jar       # Business logic JAR
microservice-webapp/target/microservice-webapp.war # Complete web application
```

### JAR Integration Verification

```bash
# Check JAR is included in WAR
jar -tf microservice-webapp/target/microservice-webapp.war | grep microservice-lib
# Output: WEB-INF/lib/microservice-lib.jar
```

The JAR library is automatically packaged inside the WAR file.

[↑ Back to Contents](#table-of-contents)

## Deployment

### TomEE Deployment

```bash
# Copy WAR to TomEE webapps directory
cp microservice-webapp/target/microservice-webapp.war /usr/local/tomee/webapps/

# TomEE automatically extracts and deploys
```

Single WAR file contains everything needed: web application + JAR library.

### Deployment Structure

```
webapps/
└── microservice-webapp/
    ├── WEB-INF/
    │   ├── lib/
    │   │   └── microservice-lib.jar    # Business logic
    │   └── web.xml
    └── META-INF/
```

JAR is automatically placed in WEB-INF/lib/ and loaded by servlet container.

### Access URLs

```
http://localhost:9292/microservice-webapp/api/service
http://localhost:9292/microservice-webapp/api/service?name=Developer
```

Clean REST-style URLs for service endpoints.

[↑ Back to Contents](#table-of-contents)

## Testing

### Unit Testing Strategy

```bash
# Test JAR library independently
cd microservice-lib && mvn test

# Test WAR integration
cd microservice-webapp && mvn test
```

Business logic can be tested without web container dependencies.

### Integration Testing

```bash
# Test deployed endpoints
curl http://localhost:9292/microservice-webapp/api/service

```

### Expected Response

```json
{
  "message": "Hello from Microservice!",
  "service": "Microservice Architecture Example",
  "version": "Microservice Library v1.0.0",
  "servlet": "com.example.webapp.MicroserviceServlet",
  "status": "success"
}
```

Complete JSON response with service information and technical details.

[↑ Back to Contents](#table-of-contents)

## Project Structure

### Complete Directory Tree

```
microservice/
├── pom.xml                              # Parent POM
├── Makefile                             # Build automation
├── README.md                            # This tutorial
├── microservice-lib/                    # JAR module
│   ├── pom.xml
│   └── src/main/java/com/example/lib/
│       └── MicroserviceLib.java         # Business logic
└── microservice-webapp/                 # WAR module
    ├── pom.xml
    ├── src/main/java/com/example/webapp/
    │   └── MicroserviceServlet.java     # Web interface
    └── src/main/webapp/WEB-INF/
        └── web.xml                      # Web configuration
```

Standard Maven multi-module project structure with clear separation of concerns.

### Module Responsibilities

- **microservice-lib**: Pure business logic, no web dependencies
- **microservice-webapp**: Web interface, HTTP handling, presentation logic
- **Parent POM**: Dependency management, build coordination

[↑ Back to Contents](#table-of-contents)

## Development Workflow

### Daily Development Commands

```bash
# Clean everything
make clean

# Build both modules
make build

# Deploy to local TomEE
make deploy

# Check deployment status
make status

# Test endpoints
make test-endpoints
```

### Development Best Practices

1. **Develop Business Logic First**: Implement and test JAR module independently
2. **Add Web Interface**: Create servlet that consumes JAR services
3. **Test Integration**: Verify JAR is properly included in WAR
4. **Deploy and Validate**: Test complete application in servlet container

### Troubleshooting

```bash
# Check JAR contents in deployed WAR
make check-war-contents

# Verify JAR is included
ls -la /usr/local/tomee/webapps/microservice-webapp/WEB-INF/lib/

# Check TomEE logs
tail -f /usr/local/tomee/logs/catalina.out
```

### Build Targets

```bash
make help              # Show available targets
make dev-build-jar     # Build only JAR library
make dev-build-war     # Build only WAR webapp
make clean             # Clean build artifacts
make test              # Run unit tests
```

Flexible build system supports both complete builds and individual module development.

[↑ Back to Contents](#table-of-contents)

---

Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
License: GNU GPL Version 2. See LICENSE