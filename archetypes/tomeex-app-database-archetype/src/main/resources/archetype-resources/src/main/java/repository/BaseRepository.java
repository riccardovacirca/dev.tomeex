package ${package}.repository;

import org.jdbi.v3.core.Jdbi;

public abstract class BaseRepository {
    protected final Jdbi jdbi;
    
    public BaseRepository() {
        this.jdbi = DatabaseManager.getInstance().getJdbi();
    }
}