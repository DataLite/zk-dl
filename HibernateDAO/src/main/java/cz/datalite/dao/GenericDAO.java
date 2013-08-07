package cz.datalite.dao;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Generic DAO design pattern.
 *
 * Based on Hibernate generic DAO recommendation, additionaly contains methods for DLSearch processing.
 *
 * @param <T> enity class
 * @param <ID>  primary key class
 *
 * @author Jiri Bubnik
 */
public interface GenericDAO<T, ID extends Serializable>
{

    /**
     * Find entity by Id using optimistic or pesimistic lock
     * @param id entity identifir
     * @param lock use pesimistic lock
     * @return requested entity
     */
    T findById(ID id, boolean lock);

    /**
     * Find entity with optimistic lock type
     * @param id entity identifier
     * @return requested entity
     */
    T findById(ID id);

    /**
     * Return the persistent instance of the given entity class with the given identifier,
     * or null if there is no such persistent instance. (If the instance is already associated
     * with the session, return that instance. This method never returns an uninitialized instance.)
     *
     * @param id an identifier
     * @return a persistent instance or null
     */
    T get(ID id);

    /**
     * Return the persistent instance of the given entity class with the given identifier,
     * or null if there is no such persistent instance. (If the instance is already associated
     * with the session, return that instance. This method never returns an uninitialized instance.)
     *
     * @param id an identifier
     * @param associationPath eager load associated entities on associationPath
     * @return a persistent instance or null
     */
    public T get( final ID id, String ... associationPath);

    /**
     * Return current state of the entity. If the entity is detached, another attached instance is returned,
     * otherwise the same instance. This is convenience method - same result would be get(entity.getId()).
     *
     * @param entity attached or detached entity.
     * @return a persistent instance or null
     */
    T get(T entity);

    /**
     * Return current state of the entity. If the entity is detached, another attached instance is returned,
     * otherwise the same instance. This is convenience method - same result would be get(entity.getId()).
     *
     * @param entity attached or detached entity.
     * @param path eager load associated entities on associationPath
     * @return a persistent instance or null
     */
    T get(T entity, String ... path);

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
    List<T> findByExample(T exampleInstance);

    /**
     * Find all entities like this example
     * @param exampleInstance example entity
     * @param excludeProperty properties to exclude
     * @return all exist similar entities
     */
    List<T> findByExample(T exampleInstance, String[] excludeProperty);

    /**
     * Write entity to the datastore. If entity exist make update,
     * if doesn't exist create it.
     * @param entity entity to save
     * @return save entity (same reference)
     */
    T makePersistent(T entity);

    /**
     * Remove entity from the datastore.
     * @param entity entity to remove
     */
    void makeTransient(T entity);

    /**
     * Attach entity to persistence context. It is useful for lazy-loading between user requests.
     * If the persistence context already contains the entity, it returns
     * the instance from persistence context. Otherwise the detached instance is attached
     * with getSession().buildLockRequest(LockOptions.NONE).lock(entity);
     *
     * @param entity entity to attach
     */
    T reattach(T entity);

    /**
     * <p>Merge entity with entity manager. It is
     * useful for lazy-loading between user requests.</p>
     * Contrary to rattach it doesn't modify original entity. Instead it gets an entity from persistence context (or gets new one from database)
     * and merges changes onto this entity. This is JPA merge() method semantics.
     *
     * @param entity entity to attach
     * @return attached entity. It is other instance then entity in parameter (either new one or earlier existing in persistence context
     */
    T merge(T entity);

    /**
     * Returns active JPA entity manager.
     * 
     * @return active JPA entity manager
     */
    EntityManager getEntityManager();

    /**
     * Returns active existing hibernate session
     * @return native hibernane session1
     */
    Session getSession();

    /**
     * Find all entities which accept criterias which is written in DLSearch.
     * Order of entities is defined in search object as well as projections
     * and row count
     * @param search object with search parameters
     * @return corresponding entities
     */
    List<T> search(DLSearch<T> search);

    /**
     * Count all entities which accept criterias which is written in DLSearch.
     * @param search search object witch search parameters
     * @return row count
     */
    Integer count(DLSearch<T> search);

    /**
     * Returns a <code>DLResponse</code> object that includes both the list of
     * results like <code>search()</code> and the total length like
     * <code>count()</code>.
     * @param search search parameters
     * @return result list with total length
     */
    DLResponse<T> searchAndCount(DLSearch<T> search);

    /**
     * <p>Write changes to the datastore.</p>
     *
     * <p>Force this session to flush. Must be called at the end of a unit of
     * work, before commiting the transaction and closing the session (depending
     * on flush-mode, Transaction.commit() calls this method).</p>
     * <p>Flushing is the process of synchronizing the underlying persistent
     * store with persistable state held in memory.</p>
     */
    void flush();

    /**
     * Completely clear the session. Evict all loaded
     * instances and cancel all pending saves, updates
     * and deletions. Do not close open iterators or instances
     * of ScrollableResults.
    
     */
    void clear();

    /**
     * Return true, if the entity is transient (check ID != null).
     *
     * @param entity entity to check
     * @return true, if entity is transient
     */
    boolean isNew(T entity);

    /**
     * Refresh the entity from the database. It works even if the entity is detached.
     *
     * @param entity entity to referesh
     */
    void refresh(T entity);
}
