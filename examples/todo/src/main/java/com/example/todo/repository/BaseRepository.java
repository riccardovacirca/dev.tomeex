package com.example.todo.repository;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {
    protected final Jdbi jdbi;
    protected final String tableName;
    protected final Class<T> entityClass;
    
    public BaseRepository(String tableName, Class<T> entityClass) {
        this.jdbi = DatabaseManager.getInstance().getJdbi();
        this.tableName = tableName;
        this.entityClass = entityClass;
    }
    
    public T save(T entity) {
        return jdbi.withHandle(handle -> {
            ID id = getId(entity);
            if (id == null) {
                return insert(handle, entity);
            } else {
                return update(handle, entity);
            }
        });
    }
    
    public Optional<T> findById(ID id) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM " + tableName + " WHERE id = :id")
                  .bind("id", id)
                  .mapToBean(entityClass)
                  .findFirst()
        );
    }
    
    public List<T> findAll() {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM " + tableName + " ORDER BY id")
                  .mapToBean(entityClass)
                  .list()
        );
    }
    
    public void deleteById(ID id) {
        jdbi.withHandle(handle ->
            handle.createUpdate("DELETE FROM " + tableName + " WHERE id = :id")
                  .bind("id", id)
                  .execute()
        );
    }
    
    public long count() {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT COUNT(*) FROM " + tableName)
                  .mapTo(Long.class)
                  .one()
        );
    }
    
    protected abstract ID getId(T entity);
    protected abstract T insert(Handle handle, T entity);
    protected abstract T update(Handle handle, T entity);
}