package cz.datalite.dao.plsql.impl;


import cz.datalite.dao.plsql.SqlLobValueFactory;
import cz.datalite.dao.plsql.StoredProcedureInvoker;
import cz.datalite.dao.plsql.StoredProcedureInvokerCreator;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.DAO;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Defaultní implementace vyvářeče spouštěčů
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@DAO
public class StoreProcedureInvokerCreatorImpl
    implements StoredProcedureInvokerCreator
{
    @Autowired
    protected SqlLobValueFactory sqlLobValueFactory;

    @Autowired
    protected TransactionAwareDataSourceProxy dataSource ;

    protected EntityManager entityManager ;

    @Autowired
    protected PlatformTransactionManager transactionManager ;


    private Integer timeout ;

    @Override
    public Integer getTimeout()
    {
        if ( timeout == null )
        {
            if ( transactionManager instanceof AbstractPlatformTransactionManager )
            {
                timeout = ((AbstractPlatformTransactionManager) transactionManager).getDefaultTimeout() ;
            }
            else
            {
                timeout = 60 ;
            }
        }

        return timeout ;
    }

    @Override
    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Setup database schema.
     * SQL object types like NUMBER_TABLE or VARCHAR_TABLE are resolved with this schema.
     */
    private String databaseSchema = "NXT";


    @PersistenceContext
    public void setEntityManager( final EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    public StoredProcedureInvoker create()
    {
        return create( dataSource ) ;
    }

    public StoredProcedureInvoker create( String name )
    {
        return create( dataSource, name ) ;
    }

    /**
     * Get default database schema.
     * SQL object types like NUMBER_TABLE or VARCHAR_TABLE are resolved with this schema.
     */
    public String getDatabaseSchema() {
        return databaseSchema;
    }

    /**
     * Setup database schema.
     * SQL object types like NUMBER_TABLE or VARCHAR_TABLE are resolved with this schema.
     */
    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }



    @Override
    public StoredProcedureInvoker create(String name, int resultType)
    {
        return setupQueryTimeout( new DefaultStoredProcedureInvoker( dataSource, name, resultType, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ) ;
    }

    private StoredProcedureInvoker setupQueryTimeout( DefaultStoredProcedureInvoker invoker )
    {
        invoker.setQueryTimeout( getTimeout() ) ;
        return invoker ;
    }

    public StoredProcedureInvoker create( DataSource dataSource )
    {
        return setupQueryTimeout(  new DefaultStoredProcedureInvoker( dataSource, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name)
    {
        return setupQueryTimeout( new DefaultStoredProcedureInvoker( dataSource, name, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name, int resultType)
    {
        return setupQueryTimeout( new DefaultStoredProcedureInvoker( dataSource, name, resultType, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ) ;
    }
}
