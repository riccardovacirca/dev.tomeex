package com.example.webapp;

import com.example.lib.MicroserviceLib;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * MicroserviceServlet - Servlet that uses the JAR library
 * 
 * This servlet demonstrates how to:
 * 1. Import a class from external JAR (MicroserviceLib)
 * 2. Use business logic separated from web logic
 * 3. Handle HTTP parameters and return responses
 */
@WebServlet("/api/service")
public class MicroserviceServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // JAR library instance - separation of concerns
    private final MicroserviceLib microserviceLib;
    
    /**
     * Constructor - initializes the service
     */
    public MicroserviceServlet() {
        // Create JAR library instance
        this.microserviceLib = new MicroserviceLib();
    }
    
    /**
     * Handles GET requests to /api/service
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configure JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS for development
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Call JAR library to get the message
        String message = microserviceLib.getGreeting();
        
        // Build JSON response
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"message\": \"").append(escapeJson(message)).append("\",");
        json.append("\"service\": \"").append(escapeJson(microserviceLib.getServiceInfo())).append("\",");
        json.append("\"version\": \"").append(escapeJson(microserviceLib.getVersion())).append("\",");
        json.append("\"servlet\": \"").append(escapeJson(this.getClass().getName())).append("\",");
        json.append("\"status\": \"success\"");
        json.append("}");
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
    
    /**
     * Handles OPTIONS requests for CORS
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Utility method to escape JSON (prevents injection)
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}