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
     * Metoda upraví nebo uloží záznam v databázi.
     * @param cisAdresa upravený nebo nový záznam pro odeslání do databáze.
     */
    void save(T entity);

    /**
     * Metoda smaže záznam z databáze.
     * @param cisAdresa záznam kola na smazání z databáze.
     */
    void delete(T entity);


    /**
     * Metoda obnoví spojení objektu cisAdresa s jeho záznamem v databázi.<br />
     * Používá se kvůli lazy inicialize, kdy dodatečně dohráváme kolekci
     * komponent v jiném requestu. Má smysl pouze s nastaveným OpenEntityManagerInView na klientovi.
     * @param cisAdresa objekt CisAdresa na znovupropojení s databází
     */
    void attachPersistenceContext(T entity);
}
