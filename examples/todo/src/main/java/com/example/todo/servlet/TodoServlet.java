package com.example.todo.servlet;

import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.service.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/todos/*")
public class TodoServlet extends HttpServlet {
    private final TodoService todoService;
    private final ObjectMapper objectMapper;
    
    public TodoServlet() {
        this.todoService = new TodoService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAll(req, resp);
            } else if (pathInfo.equals("/stats")) {
                handleGetStats(resp);
            } else if (pathInfo.startsWith("/")) {
                handleGetById(pathInfo.substring(1), resp);
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Database connection failed")) {
                handleError(resp, 503, e.getMessage());
            } else {
                handleError(resp, 500, "Internal server error: " + e.getMessage());
            }
        } catch (Exception e) {
            handleError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleCreate(req, resp);
            } else if (pathInfo.matches("/\\d+/toggle")) {
                String idStr = pathInfo.split("/")[1];
                handleToggle(idStr, resp);
            }
        } catch (ValidationException e) {
            handleError(resp, 400, e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Database connection failed")) {
                handleError(resp, 503, e.getMessage());
            } else {
                handleError(resp, 500, "Internal server error: " + e.getMessage());
            }
        } catch (Exception e) {
            handleError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                String idStr = pathInfo.substring(1);
                handleUpdate(idStr, req, resp);
            } else {
                handleError(resp, 400, "Invalid request path");
            }
        } catch (ValidationException e) {
            handleError(resp, 400, e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Database connection failed")) {
                handleError(resp, 503, e.getMessage());
            } else {
                handleError(resp, 500, "Internal server error: " + e.getMessage());
            }
        } catch (Exception e) {
            handleError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                String idStr = pathInfo.substring(1);
                handleDelete(idStr, resp);
            } else {
                handleError(resp, 400, "Invalid request path");
            }
        } catch (ValidationException e) {
            handleError(resp, 400, e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Database connection failed")) {
                handleError(resp, 503, e.getMessage());
            } else {
                handleError(resp, 500, "Internal server error: " + e.getMessage());
            }
        } catch (Exception e) {
            handleError(resp, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        String completed = req.getParameter("completed");
        String category = req.getParameter("category");
        String priority = req.getParameter("priority");
        String search = req.getParameter("search");
        
        List<Todo> todos;
        
        if (completed != null) {
            todos = todoService.findByCompleted(Boolean.parseBoolean(completed));
        } else if (category != null) {
            todos = todoService.findByCategory(category);
        } else if (priority != null) {
            todos = todoService.findByPriority(priority);
        } else if (search != null) {
            todos = todoService.searchByTitle(search);
        } else {
            todos = todoService.findAll();
        }
        
        objectMapper.writeValue(resp.getWriter(), todos);
    }
    
    private void handleGetById(String idStr, HttpServletResponse resp) 
            throws IOException {
        
        try {
            Long id = Long.parseLong(idStr);
            Optional<Todo> todo = todoService.findById(id);
            
            if (todo.isPresent()) {
                objectMapper.writeValue(resp.getWriter(), todo.get());
            } else {
                handleError(resp, 404, "Todo not found");
            }
        } catch (NumberFormatException e) {
            handleError(resp, 400, "Invalid ID format");
        }
    }
    
    private void handleGetStats(HttpServletResponse resp) throws IOException {
        TodoService.TodoStats stats = todoService.getStats();
        objectMapper.writeValue(resp.getWriter(), stats);
    }
    
    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, ValidationException {
        
        Todo todo = objectMapper.readValue(req.getInputStream(), Todo.class);
        Todo created = todoService.create(todo);
        
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), created);
    }
    
    private void handleUpdate(String idStr, HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, ValidationException {
        
        try {
            Long id = Long.parseLong(idStr);
            Todo todo = objectMapper.readValue(req.getInputStream(), Todo.class);
            Todo updated = todoService.update(id, todo);
            
            objectMapper.writeValue(resp.getWriter(), updated);
        } catch (NumberFormatException e) {
            handleError(resp, 400, "Invalid ID format");
        }
    }
    
    private void handleToggle(String idStr, HttpServletResponse resp) 
            throws IOException, ValidationException {
        
        try {
            Long id = Long.parseLong(idStr);
            Todo toggled = todoService.toggleCompleted(id);
            
            objectMapper.writeValue(resp.getWriter(), toggled);
        } catch (NumberFormatException e) {
            handleError(resp, 400, "Invalid ID format");
        }
    }
    
    private void handleDelete(String idStr, HttpServletResponse resp) 
            throws IOException, ValidationException {
        
        try {
            Long id = Long.parseLong(idStr);
            todoService.delete(id);
            
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            handleError(resp, 400, "Invalid ID format");
        }
    }
    
    private void handleError(HttpServletResponse resp, int status, String message) 
            throws IOException {
        
        resp.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("status", status);
        
        // Add error type context
        if (status == 503) {
            error.put("type", "DATABASE_CONNECTION_ERROR");
            error.put("suggestion", "Check database configuration and ensure PostgreSQL is running");
        } else if (status == 400) {
            error.put("type", "VALIDATION_ERROR");
        } else if (status == 404) {
            error.put("type", "NOT_FOUND");
        } else if (status == 500) {
            error.put("type", "INTERNAL_SERVER_ERROR");
        }
        
        objectMapper.writeValue(resp.getWriter(), error);
    }
    
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}