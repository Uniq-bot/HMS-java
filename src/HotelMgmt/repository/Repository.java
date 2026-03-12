package HotelMgmt.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * Implements Repository pattern for data access abstraction.
 * @param <T> Type of entity
 */
public interface Repository<T> {
    
    /**
     * Save an entity to storage
     */
    void save(T entity);
    
    /**
     * Update an existing entity
     */
    void update(T entity);
    
    /**
     * Delete an entity by ID
     */
    void delete(String id);
    
    /**
     * Find an entity by ID
     */
    Optional<T> findById(String id);
    
    /**
     * Get all entities
     */
    List<T> findAll();
    
    /**
     * Reload data from storage
     */
    void refresh();
}
