package cz.datalite.dao.plsql.impl;


import cz.datalite.dao.plsql.SqlLobValueFactory;
import cz.datalite.dao.plsql.StoredProcedureInvoker;
import cz.datalite.dao.plsql.StoredProcedureInvokerCreator;
import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.DAO;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

/**
 * Defaultní implementace vyvářeče spouštěčů
 */
@DAO
class StoreProcedureInvokerCreatorImpl implements StoredProcedureInvokerCreator
{
    @Autowired
    SqlLobValueFactory sqlLobValueFactory ;

    @Autowired
    TransactionAwareDataSourceProxy dataSource ;


    public StoredProcedureInvoker create()
    {
        return create( dataSource ) ;
    }

    public StoredProcedureInvoker create( String name )
    {
        return create( dataSource, name ) ;
    }

    public StoredProcedureInvoker create( DataSource dataSource )
    {
        return new DefaultStoredProcedureInvoker( dataSource, sqlLobValueFactory ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name)
    {
        return new DefaultStoredProcedureInvoker( dataSource, name, sqlLobValueFactory ) ;
    }

    public StoredProcedureInvoker create(DataSource dataSource, String name, int resultType)
    {
        return new DefaultStoredProcedureInvoker( dataSource, name, resultType, sqlLobValueFactory ) ;
    }
}
