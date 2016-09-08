package cz.datalite.service;

import cz.datalite.helpers.BooleanHelper;
import cz.datalite.helpers.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DbmsSessionPackageResetConnectionInterceptorWithSemafore implements ConnectionInterceptorWithSemafore
{
    private boolean suppressException = true ;
    private final String ZIS_CONNECTION_GUID = "ZIS_CONNECTION_GUID" ;
    private final String ZIS_RESET_PACKAGE = "ZIS_RESET_PACKAGE" ;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbmsSessionPackageResetConnectionInterceptorWithSemafore.class);


    private boolean global = false ;
    private Map<String, String> connectionsResets = new LinkedHashMap<>() ;

    @Override
    public void setGlobal(boolean value)
    {
        this.global = value ;

        if ( this.global )
        {
            connectionsResets.clear() ;
        }
    }

    /**
     * @param connection aktuální konexe
     * @param value      hodnota příznaku
     */
    private void storeResetFlag( Connection connection, String value )
    {
        if ( global )
        {
            value = "N" ;
        }

        try
        {
            String connectionGuid = connection.getClientInfo( ZIS_CONNECTION_GUID ) ;

            if ( StringHelper.isNull( connectionGuid ) )
            {
                connectionGuid = UUID.randomUUID().toString() ;
                connection.setClientInfo( ZIS_CONNECTION_GUID, connectionGuid ) ;
            }

            connectionsResets.put(connectionGuid, value) ;
        }
        catch (SQLException e)
        {
            if (!suppressException)
            {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @param connection aktuální konexe
     * @return true pokud se má volat reset package
     */
    private boolean isResetEnabled( Connection connection )
    {
        if ( global )
        {
            storeResetFlag( connection, "N" ) ;
            return true ;
        }
        else
        {
            try
            {
                String connectionGuid = connection.getClientInfo( ZIS_CONNECTION_GUID ) ;
                String value  ;

                if ( StringHelper.isNull( connectionGuid ) )
                {
                    connectionGuid = UUID.randomUUID().toString() ;
                    connection.setClientInfo( ZIS_CONNECTION_GUID, connectionGuid ) ;
                }

                if ( connectionsResets.containsKey( connectionGuid ) )
                {
                    value = connectionsResets.get( connectionGuid ) ;
                }
                else
                {
                    value = "N" ;
                }

                connectionsResets.put(connectionGuid, value ) ;

                return ( ! BooleanHelper.isTrue( value ) ) ;
            }
            catch (SQLException e)
            {
                if (!suppressException)
                {
                    throw new IllegalStateException(e);
                }
            }
        }

        return false ;
    }

    @Override
    public void onConnection(Connection connection)
    {
        LOGGER.trace( "Reset DB package" ) ;

        if ( isResetEnabled( connection ) )
        {
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

            storeResetFlag( connection, "A" ) ;
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
