package cz.datalite.service;

import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.StringHelper;
import oracle.jdbc.driver.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DbmsSessionPackageResetConnectionInterceptorWithSemafore implements ConnectionInterceptorWithSemafore
{
    private boolean suppressException = true ;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbmsSessionPackageResetConnectionInterceptorWithSemafore.class);


    private static boolean global = false ;
    private static List<String> connectionsResets = new ArrayList<>() ;


    @Override
    public void setGlobal(boolean value)
    {
        synchronized ( this )
        {
            global = value;

            if (global)
            {
                connectionsResets.clear();
            }
        }
    }

    /**
     * @param connection        konexe
     * @param add               pridat
     * @return konexe je ve fronte
     */
    private boolean isQueue( Connection connection, boolean add )
    {
        synchronized ( this )
        {
            String uuid = getConnectionId( connection ) ;

            if ( connectionsResets.contains( uuid ) )
            {
                return true ;
            }

            if (add)
            {
                connectionsResets.add( uuid ) ;
            }
        }

        return false ;
    }

    /**
     * @param connection        spojení do DB
     * @return id session
     */
    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    private String getConnectionId(Connection connection )
    {
        String uuid = null ;

        try
        {
            if ( connection instanceof OracleConnection )
            {
                OracleConnection oracleConnection = (OracleConnection) connection;

                uuid = ObjectHelper.extractString(oracleConnection.getClientData("ZIS_UUID"));

                if (uuid == null)
                {
                    uuid = UUID.randomUUID().toString();

                    oracleConnection.setClientData("ZIS_UUID", uuid);
                }
            }
        }
        catch ( Exception  e )
        {
            uuid = null ;
        }

        return StringHelper.nvl( uuid, UUID.randomUUID().toString() ) ;
    }

    /**
     * @param connection aktuální konexe
     */
    private void storeResetFlag( Connection connection )
    {
        if ( ! global )
        {
            isQueue( connection, true ) ;
        }
    }


    /**
     * @param connection aktuální konexe
     * @return true pokud se má volat reset package
     */
    private boolean isResetEnabled( Connection connection )
    {
        return ( ( global ) || ( ! isQueue( connection, false ) ) ) ;
    }

    @Override
    public void onConnection(Connection connection)
    {
        if ( isResetEnabled( connection ) )
        {
            LOGGER.trace( "Reset DB package" ) ;

            try
            {
                connection.prepareCall("begin DBMS_SESSION.RESET_PACKAGE ; end ;").execute();
            }
            catch (SQLException e)
            {
                if (!suppressException)
                {
                    throw new IllegalStateException(e);
                }
            }

            storeResetFlag( connection ) ;
        }
    }

    /**
     * @return příznak, zda je potlačena vyjímka
     */
    public boolean isSuppressException()
    {
        return suppressException;
    }

    /**
     * nastavení potlačení vyjínmky
     *
     * @param suppressException  hodnota příznaku
     */
    public void setSuppressException(boolean suppressException)
    {
        this.suppressException = suppressException;
    }
}
