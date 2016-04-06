package cz.datalite.dao.plsql.impl;


import cz.datalite.dao.plsql.SqlLobValueFactory;
import cz.datalite.dao.plsql.StoredProcedureInvoker;
import cz.datalite.dao.plsql.StoredProcedureInvokerCreator;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.DAO;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Defaultní implementace vyvářeče spouštěčů
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@DAO
class StoreProcedureInvokerCreatorImpl implements StoredProcedureInvokerCreator
{
    @Autowired
    SqlLobValueFactory sqlLobValueFactory;

    @Autowired
    TransactionAwareDataSourceProxy dataSource ;

    EntityManager entityManager ;

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

    @Override
    public StoredProcedureInvoker create(String name, int resultType)
    {
        return new DefaultStoredProcedureInvoker( dataSource, name, resultType, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ;
    }

    public StoredProcedureInvoker create( DataSource dataSource )
    {
        return new DefaultStoredProcedureInvoker( dataSource, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name)
    {
        return new DefaultStoredProcedureInvoker( dataSource, name, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name, int resultType)
    {
        return new DefaultStoredProcedureInvoker( dataSource, name, resultType, sqlLobValueFactory, getDatabaseSchema(), entityManager ) ;
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


}
