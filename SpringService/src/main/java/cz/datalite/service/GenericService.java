package cz.datalite.service;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Jiri Bubnik
 */
public interface GenericService<T, ID extends Serializable> {
    
    /**
     * Find entity by Id.
     * 
     * @param id object identifier
     * 
     * @return requested object or null
     */
    T get( ID id );

    /**
     * Get entity by instance. Typical usage is to get attached instance for detached entity.
     * <p>This is convenience method - same result would be get(entity.getId())</p>
     * <p>Typical usage is to referesh current state in new request -> entity = get(entity);
     * The method is null safe - if  null, than nothing is done and null returned.</p>
     *
     * @param entity object identifier or null
     *
     * @return same object or the attached instance. Null for null input.
     */
    T get( T entity );

    /**
     * Find entity by Id using optimistic or pesimistic lock.
     * This method may return proxy to not existing object -> fail later in lazy loading.
     *
     * @param id entity identifir
     * @param lock use pesimistic lock
     * @return the persistent instance or proxy
     */
    T findById( ID id, boolean lock );

    /**
     * Find entity with optimistic lock type.
     * This method may return proxy to not existing object -> fail later in lazy loading.
     *
     * @param id entity identifier
     * @return the persistent instance or proxy
     */
    T findById( ID id );

    /**
     * Find all entities in this table
     * @return all exist entities
     */
    List<T> findAll();

    /**
     * Find all entities like this example
     * @param exampleInstance example entity
     * @return all exist similar entities
     */
    List<T> findByExample( T exampleInstance );

    /**
     * Find all entities like this example
     * @param exampleInstance example entity
     * @param excludeProperty properties to exclude
     * @return all exist similar entities
     */
    List<T> findByExample( T exampleInstance, String[] excludeProperty );

    /**
     * Find all entities which accept criterias which is written in DLSearch.
     * Order of entities is defined in search object as well as projections
     * and row count
     * @param search object with search parameters
     * @return corresponding entities
     */
    List<T> search( DLSearch<T> search );

    /**
     * Count all entities which accept criterias which is written in DLSearch.
     * @param search search object witch search parameters
     * @return row count
     */
    Integer count( DLSearch<T> search );

    /**
     * Returns a <code>DLResponse</code> object that includes both the list of
     * results like <code>search()</code> and the total length like
     * <code>count()</code>.
     * @param search search parameters
     * @return result list with total length
     */
    DLResponse<T> searchAndCount( DLSearch<T> search );

    /**
     * Save or update the entity in database
     * @param entity entity to store
     */
    void save(T entity);

    /**
     * Delete the entity from database.
     * @param entity entity to delete.
     */
    void delete(T entity);


    /**
     * <p>Reattach the entity to the persistence context (without hitting the database).</p>
     * <p>Typical use is when you have entity instance from an old request (hence dettached) and
     * you want it to allow lazy load subseqent entities. Without reattach you will get
     * LazyInitializationException: could not initialize proxy - no Session.</p>
     * <p>If the another entity with the same ID is already in the persistence context, this method
     * uses merge (and the new entity is returned). Otherwise this entity is only attached, no query executed.</p>
     * @param entity entity to reattach.
     * @return the same object(rettached) or new entity representing same database object (if it was part of persistence context already)
     */
    T attachPersistenceContext(T entity);
}
