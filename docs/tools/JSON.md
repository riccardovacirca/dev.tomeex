# JSON

Java JSON serialization and deserialization utility providing simplified JSON operations with Jackson integration.

## Methods

String [encode](#encode)(Object obj) throws Exception
&lt;T&gt; T [decode](#decode)(String json, Class&lt;T&gt; cls) throws Exception

# Method Documentation

## encode

```java
public static String encode(Object obj) throws Exception
```

**Description:**
Converts Java object to JSON string representation. Handles complex objects, collections, and nested structures automatically.

**Parameters:**
- `obj` - Java object to serialize (can be POJO, Map, List, etc.)

**Return value:**
- `String` - JSON string representation of the object

**Exceptions:**
- `Exception` - Serialization failure, unsupported type, or Jackson configuration error

**Supported Types:**
- POJOs (Plain Old Java Objects)
- Collections (List, Set, etc.)
- Maps (HashMap, LinkedHashMap, etc.)
- Arrays
- Primitive wrappers (String, Integer, Boolean, etc.)
- Database.Record and Database.Recordset

**Example:**
```java
import dev.tomeex.tools.JSON;
import dev.tomeex.tools.Database;

// Serialize a Map
Map<String, Object> data = new HashMap<>();
data.put("name", "John Doe");
data.put("age", 30);
data.put("active", true);

String json = JSON.encode(data);
System.out.println(json);
// Output: {"name":"John Doe","age":30,"active":true}

// Serialize a Database.Record
Database db = new Database("jdbc/MyDB");
db.open();
Database.Recordset users = db.select("SELECT * FROM users WHERE id = ?", 1);
Database.Record user = users.get(0);

String userJson = JSON.encode(user);
System.out.println(userJson);
// Output: {"id":1,"name":"John Doe","email":"john@example.com",...}

db.close();

// Serialize a List
List<String> tags = Arrays.asList("java", "database", "json");
String tagsJson = JSON.encode(tags);
System.out.println(tagsJson);
// Output: ["java","database","json"]

// Serialize a custom POJO
class User {
    public String name;
    public String email;
    public boolean active;
}

User user = new User();
user.name = "Jane Doe";
user.email = "jane@example.com";
user.active = true;

String pojoJson = JSON.encode(user);
System.out.println(pojoJson);
// Output: {"name":"Jane Doe","email":"jane@example.com","active":true}
```

[↑ Methods](#methods)

## decode

```java
public static <T> T decode(String json, Class<T> cls) throws Exception
```

**Description:**
Converts JSON string to Java object of specified type. Automatically maps JSON fields to object properties.

**Parameters:**
- `json` - JSON string to deserialize
- `cls` - Target class type for deserialization

**Return value:**
- `T` - Deserialized object of type T

**Exceptions:**
- `Exception` - Invalid JSON, type mismatch, or Jackson configuration error

**Type Safety:**
- Generic method with compile-time type checking
- No need for explicit casting
- Returns exact type specified in cls parameter

**Example:**
```java
import dev.tomeex.tools.JSON;
import java.util.Map;
import java.util.List;

// Deserialize to Map
String jsonData = "{\"name\":\"John Doe\",\"age\":30,\"active\":true}";
Map<String, Object> data = JSON.decode(jsonData, Map.class);

String name = (String) data.get("name");
Integer age = (Integer) data.get("age");
Boolean active = (Boolean) data.get("active");

System.out.println("Name: " + name + ", Age: " + age + ", Active: " + active);

// Deserialize to POJO
class User {
    public String name;
    public String email;
    public boolean active;
}

String userJson = "{\"name\":\"Jane Doe\",\"email\":\"jane@example.com\",\"active\":true}";
User user = JSON.decode(userJson, User.class);

System.out.println("User: " + user.name + " <" + user.email + ">");
System.out.println("Active: " + user.active);

// Deserialize to List
String listJson = "[\"java\",\"database\",\"json\"]";
List<String> tags = JSON.decode(listJson, List.class);

System.out.println("Tags: " + tags);
for (String tag : tags) {
    System.out.println("- " + tag);
}

// Deserialize complex nested structure
class Address {
    public String city;
    public String country;
}

class Person {
    public String name;
    public int age;
    public Address address;
    public List<String> hobbies;
}

String personJson = """
{
  "name": "Alice Smith",
  "age": 28,
  "address": {
    "city": "New York",
    "country": "USA"
  },
  "hobbies": ["reading", "coding", "travel"]
}
""";

Person person = JSON.decode(personJson, Person.class);
System.out.println("Person: " + person.name + " from " + person.address.city);
System.out.println("Hobbies: " + person.hobbies);
```

[↑ Methods](#methods)

# Usage in Servlets

The JSON class is particularly useful for building REST APIs with servlets.

## REST API Example

```java
import dev.tomeex.tools.JSON;
import dev.tomeex.tools.Database;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Database db = new Database("jdbc/MyDB");
            db.open();

            // Get all users
            Database.Recordset users = db.select("SELECT * FROM users");

            // Convert to JSON and send response
            String json = JSON.encode(users);
            response.getWriter().write(json);

            db.close();

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON request body
            StringBuilder jsonBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String jsonBody = jsonBuilder.toString();

            // Deserialize JSON to User object
            UserRequest userReq = JSON.decode(jsonBody, UserRequest.class);

            // Insert into database
            Database db = new Database("jdbc/MyDB");
            db.open();

            db.query(
                "INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
                userReq.name, userReq.email, userReq.active
            );

            long userId = db.lastInsertId();

            // Prepare response
            Database.Record result = new Database.Record();
            result.put("success", true);
            result.put("userId", userId);
            result.put("message", "User created successfully");

            // Send JSON response
            response.setStatus(201);
            response.getWriter().write(JSON.encode(result));

            db.close();

        } catch (Exception e) {
            response.setStatus(500);
            Database.Record error = new Database.Record();
            error.put("success", false);
            error.put("message", "Error: " + e.getMessage());

            try {
                response.getWriter().write(JSON.encode(error));
            } catch (Exception jsonError) {
                response.getWriter().write("{\"success\":false,\"message\":\"Error encoding response\"}");
            }
        }
    }

    // DTO for request deserialization
    static class UserRequest {
        public String name;
        public String email;
        public boolean active;
    }
}
```

# Implementation Details

## Jackson ObjectMapper

The JSON class uses a single static ObjectMapper instance for optimal performance:

```java
private static final ObjectMapper mapper = new ObjectMapper();
```

**Benefits:**
- **Thread-safe** - ObjectMapper is thread-safe after configuration
- **Performance** - Avoids ObjectMapper creation overhead
- **Consistency** - Same serialization settings for all operations

## Configuration

The default ObjectMapper configuration supports:
- Standard Java types (String, Integer, Boolean, etc.)
- Collections (List, Set, Map, etc.)
- POJOs with public fields or getters/setters
- Nested objects and arrays
- null values

## Dependencies

The JSON class requires:
- **Jackson Databind** - `com.fasterxml.jackson.core:jackson-databind:2.17.1`
- Automatically included in `dev.tomeex:tools` library

Applications using `dev.tomeex:tools` get JSON functionality automatically without adding Jackson dependency to their own pom.xml.

# Best Practices

## Error Handling

Always wrap JSON operations in try-catch blocks:

```java
try {
    String json = JSON.encode(data);
    response.getWriter().write(json);
} catch (Exception e) {
    // Handle serialization errors
    response.setStatus(500);
    response.getWriter().write("{\"error\":\"Serialization failed\"}");
}
```

## Content Type

Set proper content type for JSON responses:

```java
response.setContentType("application/json");
response.setCharacterEncoding("UTF-8");
```

## Reading Request Body

Use BufferedReader to read JSON from request:

```java
StringBuilder jsonBuilder = new StringBuilder();
BufferedReader reader = request.getReader();
String line;
while ((line = reader.readLine()) != null) {
    jsonBuilder.append(line);
}
String jsonBody = jsonBuilder.toString();
```

## Type Safety

Use specific classes instead of generic Map when possible:

```java
// Better: Type-safe POJO
User user = JSON.decode(json, User.class);
String name = user.name; // No casting needed

// Less ideal: Generic Map
Map<String, Object> user = JSON.decode(json, Map.class);
String name = (String) user.get("name"); // Casting required
```

---

@2020-2025 Riccardo Vacirca - All right reserved.
