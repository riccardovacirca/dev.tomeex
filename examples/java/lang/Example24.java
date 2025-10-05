/* *****************************************************************************
 * Example24 - JSON Processing with org.json Library
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac -cp "lib/*" Example24.java
 * Run: java -cp ".:lib/*" Example24
 * ---
 * === JSON Processing Examples ===
 * 
 * 1. Basic JSON Object Operations:
 *   Nome: Mario
 *   Età: 42
 *   Attivo: true
 * 
 * 2. JSON Arrays and Nested Objects:
 *   Città: Milano
 *   Primo telefono: 02-1234567
 *   Numero telefoni: 2
 * 
 * 3. JSON Parsing from String:
 *   Result: 0 - Success
 *   Patient ID: 12345
 *   Patient Name: Anna Bianchi
 * 
 * 4. Converting Java Objects to JSON:
 *   Users array length: 2
 * 
 * 5. Error Handling and Safe Operations:
 *   JSONException caught: [error message]
 *   Safe access - existing field: value
 *   Safe check - missing field not accessed
 * 
 * === JSON Examples completed ===
 * *****************************************************************************
*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Example24 {
  
  public static void main(String[] args) {
    System.out.println("=== JSON Processing Examples ===\n");
    
    // Example 1: Basic JSON object creation and manipulation
    demonstrateBasicJSONObject();
    
    // Example 2: JSON arrays and nested structures
    demonstrateJSONArrays();
    
    // Example 3: Parsing JSON from string
    demonstrateJSONParsing();
    
    // Example 4: Converting Java objects to JSON
    demonstrateObjectToJSON();
    
    // Example 5: Error handling and validation
    demonstrateErrorHandling();
    
    System.out.println("\n=== JSON Examples completed ===");
  }
  
  /**
   * Example 1: Basic JSONObject creation and value access
   * Shows the simplest way to create and work with JSON objects
   */
  private static void demonstrateBasicJSONObject() {
    System.out.println("1. Basic JSON Object Operations:");
    
    // Create JSON object
    JSONObject patient = new JSONObject();
    patient.put("nome", "Mario");
    patient.put("cognome", "Rossi");
    patient.put("eta", 42);
    patient.put("attivo", true);
    patient.put("note", JSONObject.NULL); // Explicit null value
    
    // Access values with automatic type conversion
    String nome = patient.getString("nome");
    int eta = patient.getInt("eta");
    boolean attivo = patient.getBoolean("attivo");
    
    System.out.printf("  Nome: %s%n", nome);
    System.out.printf("  Età: %d%n", eta);
    System.out.printf("  Attivo: %b%n", attivo);
    System.out.printf("  JSON: %s%n", patient.toString());
    
    // Pretty print JSON
    System.out.printf("  JSON (formatted):%n%s%n", patient.toString(2));
    System.out.println();
  }
  
  /**
   * Example 2: Working with JSON arrays and nested objects
   * Demonstrates complex data structures with arrays and nested JSON
   */
  private static void demonstrateJSONArrays() {
    System.out.println("2. JSON Arrays and Nested Objects:");
    
    // Create nested JSON structure
    JSONObject address = new JSONObject();
    address.put("via", "Via Roma 123");
    address.put("citta", "Milano");
    address.put("cap", "20100");
    
    JSONArray telefoni = new JSONArray();
    telefoni.put("02-1234567");
    telefoni.put("347-9876543");
    
    JSONObject contact = new JSONObject();
    contact.put("id", 1001L);
    contact.put("nome", "Giulia");
    contact.put("cognome", "Verdi");
    contact.put("indirizzo", address);
    contact.put("telefoni", telefoni);
    
    // Access nested values
    String citta = contact.getJSONObject("indirizzo").getString("citta");
    String primoTelefono = contact.getJSONArray("telefoni").getString(0);
    
    System.out.printf("  Città: %s%n", citta);
    System.out.printf("  Primo telefono: %s%n", primoTelefono);
    System.out.printf("  Numero telefoni: %d%n", contact.getJSONArray("telefoni").length());
    System.out.printf("  JSON completo:%n%s%n", contact.toString(2));
    System.out.println();
  }
  
  /**
   * Example 3: Parsing JSON from string input
   * Shows how to parse JSON received from external sources
   */
  private static void demonstrateJSONParsing() {
    System.out.println("3. JSON Parsing from String:");
    
    // Simulate JSON received from API
    String jsonString = """
      {
        "result_code": 0,
        "message": "Success",
        "patient": {
          "id": 12345,
          "uuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
          "nome": "Anna",
          "cognome": "Bianchi",
          "data_nascita": "1990-03-15"
        },
        "metadata": {
          "timestamp": 1699123456789,
          "version": "1.0"
        }
      }
      """;
    
    // Parse JSON string
    JSONObject response = new JSONObject(jsonString);
    
    // Extract values from parsed JSON
    int resultCode = response.getInt("result_code");
    String message = response.getString("message");
    
    if (response.has("patient")) {
      JSONObject patient = response.getJSONObject("patient");
      long patientId = patient.getLong("id");
      String patientName = patient.getString("nome") + " " + patient.getString("cognome");
      
      System.out.printf("  Result: %d - %s%n", resultCode, message);
      System.out.printf("  Patient ID: %d%n", patientId);
      System.out.printf("  Patient Name: %s%n", patientName);
    }
    
    // Check for optional fields
    if (response.has("metadata")) {
      JSONObject metadata = response.getJSONObject("metadata");
      long timestamp = metadata.getLong("timestamp");
      System.out.printf("  Timestamp: %d%n", timestamp);
    }
    System.out.println();
  }
  
  /**
   * Example 4: Converting Java objects to JSON
   * Demonstrates creating JSON from existing Java data structures
   */
  private static void demonstrateObjectToJSON() {
    System.out.println("4. Converting Java Objects to JSON:");
    
    // Create Java objects
    User user = new User(1L, "Francesco", "Neri", "francesco.neri@email.com");
    
    List<String> skills = new ArrayList<>();
    skills.add("Java");
    skills.add("PostgreSQL");
    skills.add("REST API");
    
    Map<String, Object> preferences = new HashMap<>();
    preferences.put("theme", "dark");
    preferences.put("language", "it");
    preferences.put("notifications", true);
    
    // Convert to JSON
    JSONObject userJson = new JSONObject();
    userJson.put("id", user.getId());
    userJson.put("nome", user.getNome());
    userJson.put("cognome", user.getCognome());
    userJson.put("email", user.getEmail());
    userJson.put("skills", new JSONArray(skills));
    userJson.put("preferences", new JSONObject(preferences));
    userJson.put("created_at", System.currentTimeMillis());
    
    System.out.printf("  User JSON:%n%s%n", userJson.toString(2));
    
    // Create JSON array of multiple objects
    JSONArray users = new JSONArray();
    users.put(userJson);
    
    // Add another user directly
    JSONObject user2 = new JSONObject();
    user2.put("id", 2L);
    user2.put("nome", "Laura");
    user2.put("cognome", "Blu");
    user2.put("email", "laura.blu@email.com");
    users.put(user2);
    
    System.out.printf("  Users array length: %d%n", users.length());
    System.out.println();
  }
  
  /**
   * Example 5: Error handling and safe JSON operations
   * Shows proper exception handling and defensive programming with JSON
   */
  private static void demonstrateErrorHandling() {
    System.out.println("5. Error Handling and Safe Operations:");
    
    // Invalid JSON string
    String invalidJson = "{ invalid json structure";
    
    try {
      JSONObject obj = new JSONObject(invalidJson);
      System.out.println("  This shouldn't print - JSON was invalid");
    } catch (JSONException e) {
      System.out.printf("  JSONException caught: %s%n", e.getMessage());
    }
    
    // Safe value access
    JSONObject safeJson = new JSONObject();
    safeJson.put("existing_field", "value");
    
    // Check if field exists before accessing
    if (safeJson.has("existing_field")) {
      String value = safeJson.getString("existing_field");
      System.out.printf("  Safe access - existing field: %s%n", value);
    }
    
    if (safeJson.has("missing_field")) {
      String value = safeJson.getString("missing_field");
      System.out.printf("  This won't execute: %s%n", value);
    } else {
      System.out.println("  Safe check - missing field not accessed");
    }
    
    // Using opt methods for safe access with defaults
    String optValue = safeJson.optString("missing_field", "default_value");
    int optInt = safeJson.optInt("missing_number", 42);
    boolean optBool = safeJson.optBoolean("missing_flag", false);
    
    System.out.printf("  Optional string (with default): %s%n", optValue);
    System.out.printf("  Optional int (with default): %d%n", optInt);
    System.out.printf("  Optional boolean (with default): %b%n", optBool);
    System.out.println();
  }
  
  /**
   * Helper class for demonstrating object to JSON conversion
   */
  static class User {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    
    public User(Long id, String nome, String cognome, String email) {
      this.id = id;
      this.nome = nome;
      this.cognome = cognome;
      this.email = email;
    }
    
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
  }
}