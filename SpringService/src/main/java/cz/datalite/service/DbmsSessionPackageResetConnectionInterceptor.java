package cz.datalite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DbmsSessionPackageResetConnectionInterceptor implements ConnectionInterceptor
{
    private boolean suppressException = true ;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbmsSessionPackageResetConnectionInterceptor.class);

    @Override
    public void onConnection(Connection connection)
    {
        LOGGER.trace( "Reset DB package" ) ;

        try
        {
            connection.prepareCall( "begin DBMS_SESSION.RESET_PACKAGE ; end ;" ).execute() ;
        }
        catch (SQLException e)
        {
            if ( ! suppressException )
            {
                throw new IllegalStateException( e ) ;
            }
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
