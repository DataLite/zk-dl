package cz.datalite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DbmsSessionPackageResetConnectionInterceptorWithSemafore implements ConnectionInterceptorWithSemafore
{
    private boolean suppressException = true ;

    private final static Logger LOGGER = LoggerFactory.getLogger(DbmsSessionPackageResetConnectionInterceptorWithSemafore.class);


    private boolean enableReset = false ;

    /**
     * Nazvy zdrojů, ktere se maji resetovat
     */
    private String[] dataSourceNames ;

    @Override
    public void setEnableReset(boolean value)
    {
        if ( value != isEnableReset() )
        {
            synchronized (this)
            {
                enableReset = value;
            }
        }
    }

    @Override
    public boolean isEnableReset()
    {
        return enableReset;
    }

    @Override
    public void onConnection(Connection connection)
    {
        if ( isEnableReset() )
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
