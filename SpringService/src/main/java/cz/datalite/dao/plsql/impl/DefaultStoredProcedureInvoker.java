package cz.datalite.dao.plsql.impl;

import cz.datalite.dao.plsql.*;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * User: karny
 * Date: 1/9/12
 * Time: 3:58 PM
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DefaultStoredProcedureInvoker extends AbstractStoredProcedureInvoker
{
    public DefaultStoredProcedureInvoker(DataSource dataSource, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        super( dataSource, sqlLobValueFactory, databaseSchema, entityManager ) ;
    }

    public DefaultStoredProcedureInvoker( DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, sqlLobValueFactory, databaseSchema, entityManager ) ;

        setName( name ) ;
    }

    public DefaultStoredProcedureInvoker(DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, name, sqlLobValueFactory, databaseSchema, entityManager ) ;
        declareReturnParameter( resultType ) ;
    }

    public DefaultStoredProcedureInvoker(DataSource dataSource, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        super( dataSource, sqlLobValueFactory, databaseSchema, entityManager, extractor ) ;
    }

    public DefaultStoredProcedureInvoker( DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        this( dataSource, sqlLobValueFactory, databaseSchema, entityManager, extractor ) ;

        setName( name ) ;
    }

    public DefaultStoredProcedureInvoker( DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        this( dataSource, name, sqlLobValueFactory, databaseSchema, entityManager, extractor ) ;
        declareReturnParameter( resultType ) ;
    }
}
