package com.example.todo.service;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TodoService {
    private final TodoRepository todoRepository;
    
    public TodoService() {
        this.todoRepository = new TodoRepository();
    }
    
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    
    public Todo create(Todo todo) throws ValidationException {
        validateTodo(todo);
        
        // Set defaults for new todo
        if (todo.getCreatedAt() == null) {
            todo.setCreatedAt(LocalDateTime.now());
        }
        if (todo.getPriority() == null || todo.getPriority().trim().isEmpty()) {
            todo.setPriority("MEDIUM");
        }
        
        try {
            return todoRepository.save(todo);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Failed to setup database connection")) {
                throw new ValidationException("Database connection failed. Please check if PostgreSQL is running and properly configured.");
            }
            throw new ValidationException("Database error occurred while creating todo: " + e.getMessage());
        }
    }
    
    public Optional<Todo> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        try {
            return todoRepository.findById(id);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Failed to setup database connection")) {
                throw new RuntimeException("Database connection failed. Please check if PostgreSQL is running and properly configured.", e);
            }
            throw new RuntimeException("Database error occurred while finding todo: " + e.getMessage(), e);
        }
    }
    
    public List<Todo> findAll() {
        try {
            return todoRepository.findAll();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Failed to setup database connection")) {
                throw new RuntimeException("Database connection failed. Please check if PostgreSQL is running and properly configured.", e);
            }
            throw new RuntimeException("Database error occurred while retrieving todos: " + e.getMessage(), e);
        }
    }
    
    public List<Todo> findByCompleted(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }
    
    public List<Todo> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return List.of();
        }
        return todoRepository.findByCategory(category.trim());
    }
    
    public List<Todo> findByPriority(String priority) {
        if (priority == null || !isValidPriority(priority)) {
            return List.of();
        }
        return todoRepository.findByPriority(priority.toUpperCase());
    }
    
    public List<Todo> searchByTitle(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return todoRepository.searchByTitle(searchTerm.trim());
    }
    
    public Todo update(Long id, Todo updatedTodo) throws ValidationException {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid todo ID");
        }
        
        Optional<Todo> existing = todoRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ValidationException("Todo not found with ID: " + id);
        }
        
        validateTodo(updatedTodo);
        
        Todo todo = existing.get();
        todo.setTitle(updatedTodo.getTitle());
        todo.setDescription(updatedTodo.getDescription());
        todo.setCompleted(updatedTodo.isCompleted());
        todo.setPriority(updatedTodo.getPriority());
        todo.setCategory(updatedTodo.getCategory());
        todo.setUpdatedAt(LocalDateTime.now());
        
        return todoRepository.save(todo);
    }
    
    public Todo toggleCompleted(Long id) throws ValidationException {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid todo ID");
        }
        
        Optional<Todo> existing = todoRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ValidationException("Todo not found with ID: " + id);
        }
        
        Todo todo = existing.get();
        todo.setCompleted(!todo.isCompleted());
        todo.setUpdatedAt(LocalDateTime.now());
        
        return todoRepository.save(todo);
    }
    
    public void delete(Long id) throws ValidationException {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid todo ID");
        }
        
        Optional<Todo> existing = todoRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ValidationException("Todo not found with ID: " + id);
        }
        
        todoRepository.deleteById(id);
    }
    
    public TodoStats getStats() {
        try {
            long total = todoRepository.count();
            long completed = todoRepository.countByCompleted(true);
            long pending = todoRepository.countByCompleted(false);
            
            return new TodoStats(total, completed, pending);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Failed to setup database connection")) {
                throw new RuntimeException("Database connection failed. Please check if PostgreSQL is running and properly configured.", e);
            }
            throw new RuntimeException("Database error occurred while retrieving statistics: " + e.getMessage(), e);
        }
    }
    
    private void validateTodo(Todo todo) throws ValidationException {
        if (todo == null) {
            throw new ValidationException("Todo cannot be null");
        }
        
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new ValidationException("Todo title is required");
        }
        
        if (todo.getTitle().length() > 255) {
            throw new ValidationException("Todo title cannot exceed 255 characters");
        }
        
        if (todo.getDescription() != null && todo.getDescription().length() > 1000) {
            throw new ValidationException("Todo description cannot exceed 1000 characters");
        }
        
        if (todo.getPriority() != null && !isValidPriority(todo.getPriority())) {
            throw new ValidationException("Priority must be LOW, MEDIUM, or HIGH");
        }
        
        if (todo.getCategory() != null && todo.getCategory().length() > 100) {
            throw new ValidationException("Category cannot exceed 100 characters");
        }
    }
    
    private boolean isValidPriority(String priority) {
        if (priority == null) return false;
        String upperPriority = priority.toUpperCase();
        return "LOW".equals(upperPriority) || 
               "MEDIUM".equals(upperPriority) || 
               "HIGH".equals(upperPriority);
    }
    
    public static class TodoStats {
        private final long total;
        private final long completed;
        private final long pending;
        
        public TodoStats(long total, long completed, long pending) {
            this.total = total;
            this.completed = completed;
            this.pending = pending;
        }
        
        public long getTotal() { return total; }
        public long getCompleted() { return completed; }
        public long getPending() { return pending; }
        
        public double getCompletionRate() {
            return total == 0 ? 0.0 : (double) completed / total * 100;
        }
    }
}