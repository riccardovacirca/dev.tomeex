package ${package}.repository;

import ${package}.model.Item;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Item.class)
public interface ItemRepository {
    
    @SqlQuery("SELECT * FROM items ORDER BY created_at DESC")
    List<Item> findAll();
    
    @SqlQuery("SELECT * FROM items WHERE id = :id")
    Optional<Item> findById(@Bind("id") Long id);
    
    @SqlUpdate("INSERT INTO items (title, description, created_at, updated_at) " +
               "VALUES (:title, :description, :createdAt, :updatedAt)")
    @GetGeneratedKeys
    Long insert(@BindBean Item item);
    
    @SqlUpdate("UPDATE items SET title = :title, description = :description, " +
               "updated_at = :updatedAt WHERE id = :id")
    int update(@BindBean Item item);
    
    @SqlUpdate("DELETE FROM items WHERE id = :id")
    int deleteById(@Bind("id") Long id);
    
    @SqlQuery("SELECT COUNT(*) FROM items")
    int count();
}