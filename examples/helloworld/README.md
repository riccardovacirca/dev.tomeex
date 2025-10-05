# HelloWorld

## Table of Contents

- [Quick Start](#quick-start)
- [Project Setup](#project-setup)
- [Basic Servlet](#basic-servlet)
- [JSON Response](#json-response)
- [Add Timestamp](#add-timestamp)
- [CORS Headers](#cors-headers)
- [Handle OPTIONS Request](#handle-options-request)
- [Add Service Metadata](#add-service-metadata)
- [Build and Deploy](#build-and-deploy)
- [Java Basics for Web Dev](#java-basics-for-web-dev)
- [Common Issues](#common-issues)

## Quick Start

### API Endpoint

**GET /api/hello**

Returns a JSON hello world message with timestamp.

**Response:**
```json
{
  "message": "Hello World from HelloWorld API!",
  "timestamp": "2025-09-02T17:30:00",
  "service": "helloworld", 
  "version": "1.0.0",
  "status": "success"
}
```

### Quick Build Commands

```bash
# Build
make build

# Deploy to TomEE
make deploy

# Check status
make status

# Clean
make clean
```

### Testing the API

```bash
# Test endpoint
curl http://localhost:9292/helloworld/api/hello

# View documentation
open http://localhost:9292/helloworld/
```

Perfect for learning servlet basics and testing deployment workflows!

[↑ Back to Contents](#table-of-contents)

## Project Setup

### Project Structure Creation

```bash
mkdir helloworld
cd helloworld
mkdir -p src/main/java/com/example/servlet
mkdir -p src/main/webapp/WEB-INF
```

Creates the standard Maven directory structure for a web application project.

### Directory Tree Structure

```
helloworld/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/servlet/
│       │       └── HelloWorldServlet.java
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml
└── target/
    └── helloworld.war
```

Maven follows standard directory conventions that all Java developers recognize.

### Maven Configuration File

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>helloworld</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <failOnMissingWebXml>false</failOnMissingWebXml>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <finalName>helloworld</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

Complete Maven configuration with Jakarta servlet dependency and build plugins.

### Build Verification

```bash
mvn clean package
```

Successful build produces helloworld.war file in target directory ready for deployment.

[↑ Back to Contents](#table-of-contents)

## Basic Servlet

### Package Declaration

```java
package com.example.servlet;
```

Declares the package structure that must match the directory structure in src/main/java.

### Import Statements

```java
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
```

Imports Jakarta servlet classes and I/O classes needed for HTTP servlet functionality.

### Complete Basic Servlet

```java
package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/hello")
public class HelloWorldServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print("Hello World!");
        out.flush();
    }
}
```

Complete servlet implementation extending HttpServlet with GET request handling.

[↑ Back to Contents](#table-of-contents)

## JSON Response

### JSON Content Type

```java
response.setContentType("application/json");
response.setCharacterEncoding("UTF-8");
```

Sets HTTP response content type to JSON format with UTF-8 character encoding.

### Manual JSON Construction

```java
StringBuilder json = new StringBuilder();
json.append("{");
json.append("\"message\": \"Hello World!\",");
json.append("\"status\": \"success\"");
json.append("}");
```

Creates JSON string manually using StringBuilder with proper JSON syntax and escaping.

### Updated doGet Method

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"message\": \"Hello World!\",");
    json.append("\"status\": \"success\"");
    json.append("}");
    
    PrintWriter out = response.getWriter();
    out.print(json.toString());
    out.flush();
}
```

Updated servlet that returns JSON response instead of plain text.

[↑ Back to Contents](#table-of-contents)

## Add Timestamp

### Time Import Statements

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
```

Imports Java 8+ time classes for handling date and time operations.

### Current Timestamp Generation

```java
String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
```

Gets current date and time, formats it using ISO standard format (YYYY-MM-DDTHH:MM:SS).

### Updated JSON with Timestamp

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"message\": \"Hello World!\",");
    json.append("\"timestamp\": \"").append(timestamp).append("\",");
    json.append("\"status\": \"success\"");
    json.append("}");
    
    PrintWriter out = response.getWriter();
    out.print(json.toString());
    out.flush();
}
```

Method now includes current timestamp in the JSON response.

[↑ Back to Contents](#table-of-contents)

## CORS Headers

### CORS Header Configuration

```java
response.setHeader("Access-Control-Allow-Origin", "*");
response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
```

Adds Cross-Origin Resource Sharing headers to enable browser access from different domains.

### Updated doGet with CORS

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"message\": \"Hello World!\",");
    json.append("\"timestamp\": \"").append(timestamp).append("\",");
    json.append("\"status\": \"success\"");
    json.append("}");
    
    PrintWriter out = response.getWriter();
    out.print(json.toString());
    out.flush();
}
```

Complete method with CORS headers enabling cross-origin browser access.

[↑ Back to Contents](#table-of-contents)

## Handle OPTIONS Request

### doOptions Method Override

```java
@Override
protected void doOptions(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    response.setStatus(HttpServletResponse.SC_OK);
}
```

Overrides doOptions method to handle HTTP OPTIONS requests sent by browsers for CORS preflight.

### Servlet Structure with Multiple Methods

```java
@WebServlet("/api/hello")
public class HelloWorldServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle GET requests
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle CORS preflight
    }
}
```

Servlet can handle multiple HTTP methods by overriding corresponding do* methods.

[↑ Back to Contents](#table-of-contents)

## Add Service Metadata

### Enhanced JSON Response

```java
StringBuilder json = new StringBuilder();
json.append("{");
json.append("\"message\": \"Hello World from HelloWorld API!\",");
json.append("\"timestamp\": \"").append(timestamp).append("\",");
json.append("\"service\": \"helloworld\",");
json.append("\"version\": \"1.0.0\",");
json.append("\"status\": \"success\"");
json.append("}");
```

Enhanced JSON response with service metadata for better API design.

### API Design Principles

```json
{
  "message": "Descriptive user-facing message",
  "timestamp": "Request processing time",
  "service": "Service identification",
  "version": "API version for compatibility",
  "status": "Success/error indicator"
}
```

Standard fields provide consistent API responses across services.

### Expected Enhanced Output

```json
{
  "message": "Hello World from HelloWorld API!",
  "timestamp": "2024-01-01T12:34:56.789",
  "service": "helloworld",
  "version": "1.0.0",
  "status": "success"
}
```

Complete response with metadata enabling better client integration and debugging.

[↑ Back to Contents](#table-of-contents)

## Build and Deploy

### Makefile Variables

```makefile
APP_NAME = helloworld
CONTAINER_NAME = tomee-dev
TOMEE_WEBAPPS = /usr/local/tomee/webapps
```

Defines application name, container name, and TomEE webapps directory path.

### Complete Makefile

```makefile
APP_NAME = helloworld
CONTAINER_NAME = tomee-dev
TOMEE_WEBAPPS = /usr/local/tomee/webapps

.PHONY: clean build deploy status help

help:
	@echo "Targets: clean, build, deploy, status"

clean:
	@mvn clean -q

build:
	@echo "Building $(APP_NAME)..."
	@mvn package -q -DskipTests
	@echo "Build complete: target/$(APP_NAME).war"

deploy: build
	@echo "Deploying to $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@cp target/$(APP_NAME).war $(TOMEE_WEBAPPS)/
	@echo "Deployed: http://localhost:9292/$(APP_NAME)"

status:
	@ls -la $(TOMEE_WEBAPPS)/$(APP_NAME)* 2>/dev/null || echo "Not deployed"
```

Complete Makefile for automated build and deployment workflow.

### Development Workflow

```bash
make clean      # Remove build artifacts
make build      # Compile and package
make deploy     # Deploy to TomEE
make status     # Check deployment
```

Standard development workflow commands for building and deploying application.

[↑ Back to Contents](#table-of-contents)

## Java Basics for Web Dev

### Package Structure

```java
package com.example.servlet;
```

Package declaration must match directory structure in src/main/java/com/example/servlet/.

### Class Inheritance

```java
public class HelloWorldServlet extends HttpServlet {
```

Class inherits from HttpServlet to gain HTTP request handling capabilities.

### Method Override

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
```

Override annotation indicates method replaces parent class implementation.

### StringBuilder Usage

```java
StringBuilder json = new StringBuilder();
json.append("{");
json.append("\"key\": \"value\"");
json.append("}");
String result = json.toString();
```

StringBuilder provides efficient string concatenation for building large strings.

### Exception Declaration

```java
protected void doGet(...) throws ServletException, IOException {
```

Method declares it may throw exceptions. TomEE container handles thrown exceptions.

[↑ Back to Contents](#table-of-contents)

## Common Issues

### Build Environment Issues

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
sudo apt install maven
mvn clean package
```

Sets JAVA_HOME environment variable, installs Maven, and rebuilds project cleanly.

### 404 Not Found Error

```
Servlet: @WebServlet("/api/hello")
URL: http://localhost:9292/helloworld/api/hello
Format: http://host:port/app-name/servlet-path
```

Verifies URL matches servlet annotation mapping and follows correct format.

### 500 Internal Server Error

```bash
tail -f logs/localhost_access_log.*.txt
```

Monitors TomEE access logs to identify internal server errors and exceptions.

### CORS Browser Errors

```java
// Required in both doGet() and doOptions()
response.setHeader("Access-Control-Allow-Origin", "*");
response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
```

Ensures CORS headers are set in all HTTP method handlers to prevent browser blocking.

### Common Error Causes

```
- Java compilation errors
- Missing import statements
- Exception in servlet code
- Wrong package structure
- Incorrect URL mapping
- Missing CORS headers
```

Lists frequent issues that cause servlet failures and HTTP errors.

[↑ Back to Contents](#table-of-contents)

---

Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
License: GNU GPL Version 2. See LICENSE