package cz.datalite.dao.plsql.impl;

import cz.datalite.dao.plsql.SqlLobValueFactory;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * User: karny
 * Date: 1/9/12
 * Time: 3:58 PM
 */
@SuppressWarnings("WeakerAccess")
class DefaultStoredProcedureInvokerBefore11 extends AbstractStoredProcedureInvoker
{
    public DefaultStoredProcedureInvokerBefore11(DataSource dataSource, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
	{
		super( dataSource, sqlLobValueFactory, databaseSchema, entityManager ) ;

        getJdbcTemplate().setNativeJdbcExtractor(new CommonsDbcpNativeJdbcExtractor());
	}

    public DefaultStoredProcedureInvokerBefore11(DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, sqlLobValueFactory, databaseSchema, entityManager ) ;

        setName( name ) ;
    }

    public DefaultStoredProcedureInvokerBefore11(DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, name, sqlLobValueFactory, databaseSchema, entityManager ) ;
        declareReturnParameter( resultType ) ;
    }
}
