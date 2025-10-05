package com.example.todo.repository;

import com.example.todo.model.Todo;
import org.jdbi.v3.core.Handle;

import java.time.LocalDateTime;
import java.util.List;

public class TodoRepository extends BaseRepository<Todo, Long> {
    
    public TodoRepository() {
        super("todos", Todo.class);
    }
    
    @Override
    protected Long getId(Todo todo) {
        return todo.getId();
    }
    
    @Override
    protected Todo insert(Handle handle, Todo todo) {
        Long newId = handle
            .createUpdate("INSERT INTO todos (title, description, completed, created_at, priority, category) " +
                         "VALUES (:title, :description, :completed, :createdAt, :priority, :category)")
            .bind("title", todo.getTitle())
            .bind("description", todo.getDescription())
            .bind("completed", todo.isCompleted())
            .bind("createdAt", todo.getCreatedAt())
            .bind("priority", todo.getPriority())
            .bind("category", todo.getCategory())
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Long.class)
            .one();
        
        todo.setId(newId);
        return todo;
    }
    
    @Override
    protected Todo update(Handle handle, Todo todo) {
        todo.setUpdatedAt(LocalDateTime.now());
        
        handle.createUpdate("UPDATE todos SET " +
                           "title = :title, " +
                           "description = :description, " +
                           "completed = :completed, " +
                           "updated_at = :updatedAt, " +
                           "completed_at = :completedAt, " +
                           "priority = :priority, " +
                           "category = :category " +
                           "WHERE id = :id")
            .bind("title", todo.getTitle())
            .bind("description", todo.getDescription())
            .bind("completed", todo.isCompleted())
            .bind("updatedAt", todo.getUpdatedAt())
            .bind("completedAt", todo.getCompletedAt())
            .bind("priority", todo.getPriority())
            .bind("category", todo.getCategory())
            .bind("id", todo.getId())
            .execute();
        
        return todo;
    }
    
    // Custom query methods
    public List<Todo> findByCompleted(boolean completed) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM todos WHERE completed = :completed ORDER BY created_at DESC")
                .bind("completed", completed)
                .mapToBean(Todo.class)
                .list()
        );
    }
    
    public List<Todo> findByCategory(String category) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM todos WHERE category = :category ORDER BY created_at DESC")
                .bind("category", category)
                .mapToBean(Todo.class)
                .list()
        );
    }
    
    public List<Todo> findByPriority(String priority) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM todos WHERE priority = :priority ORDER BY created_at DESC")
                .bind("priority", priority)
                .mapToBean(Todo.class)
                .list()
        );
    }
    
    public List<Todo> searchByTitle(String searchTerm) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM todos WHERE LOWER(title) LIKE LOWER(:search) ORDER BY created_at DESC")
                .bind("search", "%" + searchTerm + "%")
                .mapToBean(Todo.class)
                .list()
        );
    }
    
    public long countByCompleted(boolean completed) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT COUNT(*) FROM todos WHERE completed = :completed")
                .bind("completed", completed)
                .mapTo(Long.class)
                .one()
        );
    }
}