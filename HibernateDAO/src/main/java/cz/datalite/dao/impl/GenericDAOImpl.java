package cz.datalite.dao.impl;

import cz.datalite.dao.*;
import cz.datalite.dao.support.JpaEntityInformation;
import cz.datalite.dao.support.JpaEntityInformationSupport;
import cz.datalite.hibernate.OrderBySqlFormula;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Iterator;
import java.util.List;

/*
 * Generic DAO design pattern.
 *
 * Based on Hibernate generic DAO recommendation, additionaly contains methods for DLSearch processing.
 *
 * @author Jiri Bubnik
 */
@SuppressWarnings({"unchecked", "InstantiatingObjectToGetClassObject"})
public class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenericDAOImpl.class);

    public static final String INVALID_ARGUMENT_ID_MISSING = "Invalid argument in GenericDAO. Id is missing";
    private static final String INVALID_ARGUMENT_ENTITY_MISSING = "Invalid argument in GenericDAO. Entity is missing";
    private static final String INVALID_ARGUMENT_EXAMPLE_MISSING = "Invalid argument in GenericDAO. Example is missing";
    private static final String INVALID_ARGUMENT_SEARCH_OBJECT_MISSING = "Invalid argument in GenericDAO. Search object is missing";
    private final Class<T> persistentClass;
    private EntityManager entityManager;
    private JpaEntityInformation<T, ?> entityInformation;


    /**
     * Default Constructor - resolves persistent class from generics.
     * 
     * @throws IllegalStateException if no generics is found
     */
    public GenericDAOImpl()
    {
        ParameterizedType parametrizedType = getParameterType( getClass() ) ;

        if ( parametrizedType.getActualTypeArguments()[0] instanceof Class )
        {
            this.persistentClass = (Class<T>) parametrizedType.getActualTypeArguments()[0];
        }
        else if (parametrizedType.getActualTypeArguments()[0] != null)
        {
            this.persistentClass = (Class<T>) getRawType( parametrizedType.getActualTypeArguments()[ 0 ] ) ;
        }
        else
        {
            throw new IllegalStateException( "GenericDAOImpl - class " + getClass() + " - " + parametrizedType.getActualTypeArguments()[0].toString() ) ;
        }
    }

    /**
     * @param clazz     třída
     * @return typ třídy
     */
    private ParameterizedType getParameterType( Class clazz )
    {
        if ( clazz.getGenericSuperclass() instanceof ParameterizedType) // class
        {
            return (ParameterizedType) clazz.getGenericSuperclass();
        }
        else if ( ( clazz.getGenericSuperclass() instanceof Class ) && ( clazz.getGenericSuperclass() != Class.class )
                && ( clazz.getGenericSuperclass() != Object.class ) )
        {
            return getParameterType( (Class)clazz.getGenericSuperclass() ) ;
        }
        else
        {
            throw new IllegalStateException("GenericDAOImpl - class " + getClass() + " is not subtype of ParametrizedType - " + getClass().getGenericSuperclass() );
        }
    }

    /**
     * @param t  typ parametru
     * @return trida parametru
     */
    private Class<?> getRawType( Type t )
    {
        if( t instanceof Class<?> )
        {
            return ( Class<?> ) t;
        }
        if( t instanceof WildcardType)
        {
            WildcardType wt = ( WildcardType ) t;
            Type[ ] upperBounds = wt.getUpperBounds( );

            if( upperBounds != null && upperBounds.length == 1 )
            {
                return getRawType( upperBounds[ 0 ] );
            }
        }
        if( t instanceof GenericArrayType)
        {
            try
            {
                return Class.forName( "[L" + getRawType( ( ( GenericArrayType ) t ).getGenericComponentType( ) ).getName( ) + ";" );
            }
            catch( ClassNotFoundException e )
            {
                return new Object[ 0 ].getClass( );
            }
        }
        if( t instanceof ParameterizedType )
        {
            return ( Class<?> ) ( ( ParameterizedType ) t ).getRawType( );
        }
        if( t instanceof TypeVariable<?>)
        {
            TypeVariable<?> tv = ( TypeVariable<?> ) t;
            Type[ ] bounds = tv.getBounds( );
            if( bounds != null && bounds.length == 1 )
            {
                return getRawType( bounds[ 0 ] );
            }
        }
        return Object.class;
    }

    public GenericDAOImpl( final Class<T> persistentClass ) {
        this.persistentClass = persistentClass;
    }

    @PersistenceContext
    public void setEntityManager( final EntityManager entityManager ) {
        this.entityManager = entityManager;

        try {
            this.entityInformation = JpaEntityInformationSupport.getMetadata(persistentClass, entityManager);
        } catch (Exception e) {
            LOGGER.warn("Unable to load entityInformation from metamodel, get(Entity) method will not be available.", e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Session getSession() {
        return ( Session ) getEntityManager().getDelegate();
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public T get( final ID id ) {
        assert id != null : INVALID_ARGUMENT_ID_MISSING;
        return ( T ) getSession().get( getPersistentClass(), id );
    }

    public T get( final ID id, String ... associationPath) {
        assert id != null : INVALID_ARGUMENT_ID_MISSING;
        Criteria criteria = getSession().createCriteria( getPersistentClass() );
        criteria.add(Restrictions.idEq(id));

        for (String p : associationPath)
            criteria.setFetchMode(p, FetchMode.JOIN);

        return ( T ) criteria.uniqueResult();
    }

    public T get( final T entity ) {
        assert entity != null : INVALID_ARGUMENT_ID_MISSING;
        if (entityInformation == null) {
            throw new IllegalStateException("Entity information is not available, unable to resolve ID column.");
        }
        return ( T ) getSession().get( getPersistentClass(), entityInformation.getId(entity) );
    }

    public T get( final T entity, String ... path ) {
        assert entity != null : INVALID_ARGUMENT_ID_MISSING;
        if (entityInformation == null) {
            throw new IllegalStateException("Entity information is not available, unable to resolve ID column.");
        }

        Criteria criteria = getSession().createCriteria( getPersistentClass() );
        criteria.add(Restrictions.idEq( entityInformation.getId(entity) ));

        for (String p : path)
            criteria.setFetchMode(p, FetchMode.JOIN);

        return ( T ) criteria.uniqueResult();
    }

    public T findById( final ID id ) {
        assert id != null : INVALID_ARGUMENT_ID_MISSING;
        return findById( id, false );
    }

    public T findById( final ID id, final boolean lock ) {
        assert id != null : INVALID_ARGUMENT_ID_MISSING;
        T entity;
        if ( lock ) {
            entity = ( T ) getSession().load( getPersistentClass(), id, LockOptions.UPGRADE);
        } else {
            entity = ( T ) getSession().load( getPersistentClass(), id );
        }

        return entity;
    }

    public T findById(final ID id, int timeout) {
        assert id != null : INVALID_ARGUMENT_ID_MISSING;
        LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        lockOptions.setTimeOut(timeout);
        T entity = (T) getSession().load(getPersistentClass(), id, lockOptions);

        return entity;
    }


    public List<T> findAll() {
        return findByCriteria();
    }

    public List<T> findByExample( final T exampleInstance ) {
        assert exampleInstance != null : INVALID_ARGUMENT_EXAMPLE_MISSING;
        return findByExample( exampleInstance, new String[]{} );
    }

    public List<T> findByExample( final T exampleInstance, final String[] excludeProperty ) {
        assert exampleInstance != null : INVALID_ARGUMENT_EXAMPLE_MISSING;
        assert excludeProperty != null : "Invalid argument in GenericDAO. PropertyArray is missing";
        final Criteria crit = getSession().createCriteria( getPersistentClass() );
        final Example example = Example.create( exampleInstance );
        for ( String exclude : excludeProperty ) {
            example.excludeProperty( exclude );
        }
        crit.add( example );
        return crit.list();
    }

    public T makePersistent( final T entity ) {
        assert entity != null : INVALID_ARGUMENT_ENTITY_MISSING;
        getSession().saveOrUpdate( entity );
        return entity;
    }

    public void makeTransient( final T entity ) {
        assert entity != null : INVALID_ARGUMENT_ENTITY_MISSING;

        // JPA forbids deletion of detached object (although in Hibernate itself it is legal).
        if (!getSession().contains( entity ))
            reattach(entity);

        getSession().delete( entity );
    }

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param criterion list of criterions
     * @return found entity list
     */
    protected List<T> findByCriteria( final Criterion... criterion ) {
        final Criteria crit = getSession().createCriteria( getPersistentClass() );
        for ( Criterion c : criterion ) {
            crit.add( c );
        }
        return crit.list();
    }

    public T reattach( final T entity ) {
        assert entity != null : INVALID_ARGUMENT_ENTITY_MISSING;

        try
        {
            // attach the persistence context - Hibernate will presume, that the entity is NOT modified.
            getSession().buildLockRequest(LockOptions.NONE).lock(entity);
            return entity;
        }
        catch (NonUniqueObjectException e)
        {
            // It would be cleaner to check if the entity is in already in the persistence context
            // unfortunatelly, there is no public method that I am aware of.
            return (T) getSession().get(entity.getClass(), e.getIdentifier());
        }
    }

    public T merge( final T entity ) {
        assert entity != null : INVALID_ARGUMENT_ENTITY_MISSING;
        return ( T ) getSession().merge( entity );
    }

    public List<T> search( final DLSearch<T> search ) {
        assert search != null : INVALID_ARGUMENT_SEARCH_OBJECT_MISSING;

        // prepare executable criteria
        final Criteria executable = DetachedCriteria.forClass( persistentClass ).getExecutableCriteria( getSession() );
        addSearchCriteria( executable, search, true );

        return search( executable, search );
    }

    /**
     * Add firstResult/maxResults and distinct flags and executes the query.
     *
     * @param executable the criterias
     * @param search search object
     * @return result
     */
    protected List<T> search( final Criteria executable, final DLSearch<T> search ) {

        // if row count is defined write it
        if ( search.getRowCount() != 0 ) {
            executable.setMaxResults( search.getRowCount() );
            executable.setFirstResult( search.getFirstRow() );
        }

        if ( search.isDistinct() ) {
            executable.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY );
        }

        addCacheMode(executable, search);

        //execute
        return executable.list();
    }

    public Integer count( final DLSearch<T> search ) {
        assert search != null : INVALID_ARGUMENT_SEARCH_OBJECT_MISSING;

        // prepare executable criteria
        final Criteria executable = DetachedCriteria.forClass( persistentClass ).getExecutableCriteria( getSession() );
        addSearchCriteria( executable, search, false );


        addCacheMode(executable, search);

        // execute and get row count number
        return count( executable );
    }

    /**
     * Adds projection of rowCount and executes the criteria query. Returns count from database or 0.
     *
     * <b>This method modifies the criteria object (adds projection to rowcount).
     * Use after all other criteria operations (e.g. search).</b>
     *
     * @param executable criteria initialized criteria query.
     * @return count from database or 0.
     */
    protected Integer count( final Criteria executable ) {
        // set projection for row count only
        executable.setProjection( Projections.rowCount() );

        // clear distinct and pagination flags
        executable.setResultTransformer( Criteria.ROOT_ENTITY );
        executable.setMaxResults( 1 );
        executable.setFirstResult( 0 );


        final List cnt = executable.list();

        // hibernate doesn't throw exception when something goes wrong
        if ( cnt.isEmpty() ) {
            return 0;
        }
        // converts row count number from long to int
        Object result = cnt.get( 0 );
        // result type of count depends on Hibernate version
        if (result instanceof Long)
            return (( Long ) cnt.get( 0 )).intValue();
        else
            return (Integer) cnt.get(0);
    }

    /**
     * Method preparse DetachedCriteria from DLSearch using all aliasses,
     * projections, criterions and if writeOrderBy is set then also with
     * all sorts.
     *
     * @param criteria criteria object to add aliases and restrictions
     * @param search search object with data
     * @param writeOrderBy use sorts
     */
    protected void addSearchCriteria( final Criteria criteria, final DLSearch<T> search, final boolean writeOrderBy ) {
        assert search != null : INVALID_ARGUMENT_SEARCH_OBJECT_MISSING;

        // add sort aliases
        for ( final Iterator<DLSort> it = search.getSorts(); it.hasNext(); ) {
            final DLSort sort = it.next();
            if (sort.getColumn() != null)
                search.addAliasesForProperty(sort.getColumn(), JoinType.LEFT_OUTER_JOIN);
        }

        // write aliases
        for ( DLSearch.Alias alias : search.getAliases() ) {
            criteria.createAlias( alias.getPath(), alias.getAlias(), alias.getJoinType() );
        }

        // write projections
        if ( !search.getProjections().isEmpty() ) { // writes list of projections
            final ProjectionList projectionList = Projections.projectionList();
            for ( Projection projection : search.getProjections() ) {
                projectionList.add( projection );
            }
            criteria.setProjection( projectionList );
        }

        // write criterions
        for ( Criterion criterion : search.getCriterions() ) {
            if ( criterion != null ) {
                criteria.add( criterion );
            }
        }

        // write order by
        if ( writeOrderBy ) {
            for ( final Iterator<DLSort> it = search.getSorts(); it.hasNext(); ) {
                final DLSort sort = it.next();
                if (sort.getSqlFormula() != null) {
                    // custom sort
                    criteria.addOrder(OrderBySqlFormula.sqlFormula(sort.getSqlFormula()));
                } else {
                    switch ( sort.getSortType() ) {
                        case ASCENDING:
                            criteria.addOrder(new OrderNullPrecedence(search.getAliasForFullPath(sort.getColumn()), true, sort.getNullPrecedence()));
                            break;
                        case DESCENDING:
                            criteria.addOrder(new OrderNullPrecedence(search.getAliasForFullPath(sort.getColumn()), false, sort.getNullPrecedence()));
                            break;
                        default:
                    }
                }
            }
        }
    }

//    protected NullPrecedence getNullPrecedence(DLNullPrecedence dlNullPrecedence) {
//        return NullPrecedence.NONE;
//    }

    public DLResponse<T> searchAndCount( final DLSearch<T> search ) {
        assert search != null : INVALID_ARGUMENT_SEARCH_OBJECT_MISSING;

        List<T> result = search( search );
        int cnt;

        // if page actual results < size, we can use results, othewise go to the database
        if (result.size() < search.getRowCount())
            cnt = search.getFirstRow() + result.size();
        else
            cnt = count( search );

        return new DLResponse<T>( result, cnt );
    }

    /**
     * Execute criteria query - get result list (pagination from search object) and count results).
     *
     * <b>This methods modifies criteria object. Projections and paginations are set. Don't use this object after returning.</b>
     *
     * @param criteria criteria query specification
     * @param search search object (used for pagination and distinct flag)
     *
     * @return result list and count in DLResponse object
     */
    protected DLResponse<T> searchAndCount( final Criteria criteria, final DLSearch<T> search ) {
        addCacheMode(criteria, search);
        return new DLResponse<T>( search( criteria, search ), count( criteria ) );
    }

    /**
     * Enables cacheable to criteria and set cacheMode to criteria
     * @param criteria
     * @param search
     */
    private void addCacheMode(Criteria criteria, DLSearch<T> search) {
        if (search.getCacheMode() != null) {
            criteria.setCacheable(true);
            criteria.setCacheMode(search.getCacheMode());
        }
    }

    public boolean isNew( T entity ) {
        assert entity != null : INVALID_ARGUMENT_ENTITY_MISSING;

        try {
            return getSession().getIdentifier( entity ) == null;
        } catch ( TransientObjectException ex ) {
            return true;
        }
    }

    public void refresh( T entity ) {
        assert entity != null : "Invalid argument in GenericDAO refresh(). Entity is missing";
        assert !isNew( entity ) : "Invalid argument in GenericDAO refresh(). Entity is transient";

        getSession().refresh(entity);
    }

    public int updateHQL(String update, Object... values) {
        Query q = getSession().createQuery(update);
        int i = 0;
        for (Object val : values) {
            q.setParameter(i++, val);
        }
        return q.executeUpdate();
    }
}
