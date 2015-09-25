package cz.datalite.service.impl;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.helpers.StringHelper;
import cz.datalite.model.DataSourceDescriptor;
import cz.datalite.service.DataSourceSwitcherService;
import cz.datalite.service.SwitchDataSourceAction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractDataSourceSwitcherService extends AbstractRoutingDataSource implements DataSourceSwitcherService, ApplicationContextAware
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDataSourceSwitcherService.class);

    /**
     * Příznak zda povolit vnucení uživatele při vyvtáření spojení do DB
     */
    UserDetailsService userDetailService;

    /**
     * Název třídy pro získání uživatele
     */
    String userDetailsServiceName;

    /**
     * Aplikační kontext
     */
    ApplicationContext applicationContext;

    /**
     * Datové zdroje
     */
    Map<String, Object> dataSources ;

    /**
     * Popis datových zdrojů
     */
    List<DataSourceDescriptor> dataSourceDescriptors = new LinkedList<>() ;

    /**
     * Uložiště aktuálního datového zdroje
     */
    final ThreadLocal<String> dataSourceName = new ThreadLocal<>() ;


    EntityManagerFactory entityManagerFactory ;

    /**
     * @param userDetailsServiceName Název třídy pro získání uživatele
     */
    public AbstractDataSourceSwitcherService(String userDetailsServiceName)
    {
        this.userDetailsServiceName = userDetailsServiceName;
    }

    /**
     * @param userDetailsService  implementace služby pro získání uživatele
     */
    public AbstractDataSourceSwitcherService(UserDetailsService userDetailsService)
    {
        this.userDetailService = userDetailsService ;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext ;
    }

    @Override
    public void afterPropertiesSet()
    {
        super.afterPropertiesSet() ;
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources)
    {
        super.setTargetDataSources(targetDataSources);

        this.dataSources = new LinkedHashMap<>() ;

        if (targetDataSources != null)
        {
            for ( Map.Entry<Object, Object> o : targetDataSources.entrySet())
            {
                this.dataSources.put((String) o.getKey(), o.getValue() ) ;
            }
        }
    }

    /**
     * Nastavení popisu datových zdrojů
     *
     * @param dataSourceDescriptors     popis datových zdrojů
     */
    public void setDataSourceDescriptors(Map<String, String> dataSourceDescriptors)
    {
        this.dataSourceDescriptors = new LinkedList<>() ;

        for( Map.Entry<String, String> entry : dataSourceDescriptors.entrySet() )
        {
            this.dataSourceDescriptors.add(new DataSourceDescriptor(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    protected Object determineCurrentLookupKey()
    {
        return dataSourceName.get() ;
    }

    @Override
    public boolean isAvailable(String name)
    {
        return (dataSources != null) && (dataSources.containsKey(name));
    }

    /**
     * @return služba pro získání přihlašovacího jména
     */
    private UserDetailsService getUserDetailService()
    {
        if ( (userDetailService == null) && ( applicationContext != null ) && (!StringHelper.isNull(userDetailsServiceName)) )
        {
            try
            {
                userDetailService = (UserDetailsService) applicationContext.getBean(userDetailsServiceName);
            }
            catch (Exception e)
            {
                userDetailService = null;
            }
        }

        return userDetailService;
    }

    /**
     * @return továrna na manažery entit
     */
    private EntityManagerFactory getEntityManagerFactory()
    {
        if ( ( applicationContext != null ) && ( entityManagerFactory == null ) )
        {
            entityManagerFactory = applicationContext.getBean( EntityManagerFactory.class ) ;
        }

        return entityManagerFactory;
    }

    /**
     * Přihlášení do hibernate session
     * @return předchozí spojení
     */
    private EntityManagerHolder connectSession( String name )
    {
        if ( ! EqualsHelper.isEqualsNull(name, dataSourceName.get()) )
        {
            dataSourceName.set(name);

            EntityManagerHolder entityManagerHolder = null;

            if (getEntityManagerFactory() != null)
            {
                // if entity manager is not yet set
                if (TransactionSynchronizationManager.hasResource(getEntityManagerFactory()))
                {
                    entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(getEntityManagerFactory());
                    TransactionSynchronizationManager.unbindResource(getEntityManagerFactory());
                }

                EntityManager entityManager = getEntityManagerFactory().createEntityManager() ;

                TransactionSynchronizationManager.bindResource(getEntityManagerFactory(), new EntityManagerHolder(entityManager));

                LOGGER.info("Switch hibernate session to " + ((dataSourceName.get() == null) ? "default" : dataSourceName.get()) ) ;
            }

            return entityManagerHolder;
        }

        return null ;
    }

    /**
     * Odhlášení hibernate session
     */
    private void disconnectSession( EntityManagerHolder entityManagerHolder )
    {
        if ( getEntityManagerFactory() != null )
        {
            // if entity manager is not yet set
            if ( TransactionSynchronizationManager.hasResource(getEntityManagerFactory() ) )
            {
                EntityManagerHolder current = (EntityManagerHolder) TransactionSynchronizationManager.getResource(getEntityManagerFactory()) ;

                TransactionSynchronizationManager.unbindResource(getEntityManagerFactory()) ;

                if ( ( current != null ) && ( current != entityManagerHolder ) )
                {
                    Session hibernateSession = (Session) current.getEntityManager().getDelegate();

                    if ((hibernateSession.isConnected()) && (hibernateSession.isOpen()))
                    {
                        LOGGER.debug("Switch hibernate session from " + ((dataSourceName.get() == null) ? "default" : dataSourceName.get()));
                        hibernateSession.disconnect() ;
                        current.getEntityManager().close() ;
                    }
                }
            }

            dataSourceName.remove() ;

            if ( entityManagerHolder != null )
            {
                TransactionSynchronizationManager.bindResource(getEntityManagerFactory(), entityManagerHolder);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return getConnection( dataSourceName.get() ) ;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        return getConnection( dataSourceName.get(), username, password);
    }

    /**
     *
     * @param name  název datového zdroje
     * @return datový zdroj
     */
    protected DataSource getDataSource( String name )
    {
        return ( name == null ) ? determineTargetDataSource() : resolveSpecifiedDataSource( dataSources.get( name ) ) ;
    }

    /**
     *
     * @param database      jméno datového zdroje
     * @param username      přihlašovací jméno
     * @param password      příhlasšovací heslo
     * @return spojení do db
     * @throws SQLException
     */
    protected Connection getConnection( String database, String username, String password) throws SQLException
    {
        return getDataSource( database ).getConnection( username, password ) ;
    }

    /**
     * Příznak zda posilat jméno uživatele do přihlášení do DB
     */
    boolean awareUserName = true;

    /**
     * @return aktulně přihlášený uživatel
     */
    protected abstract UserDetails getUserDetails() ;

    /**
     * @param database              jméno datového zdroje
     * @return spojení do DB
     * @throws SQLException
     */
    protected Connection getConnection( String database ) throws SQLException
    {
        if ((getUserDetailService() != null) && (awareUserName))
        {
            UserDetails userDetails = getUserDetails();

            if ( database != null )
            {
                String loginName = (userDetails != null) ? userDetails.getUsername() : null;

                if (loginName != null)
                {
                    awareUserName = false;
                    userDetails = getUserDetailService().loadUserByUsername(loginName);
                    LOGGER.debug("Refresh user details: " + userDetails.getUsername());
                    awareUserName = true;
                }
            }

            if (userDetails != null)
            {
                try
                {
                    Connection connection = getDataSource(database).getConnection(userDetails.getUsername(), userDetails.getPassword());

                    LOGGER.debug("Get connection " + ( database == null ? "" : database ) + " for " + userDetails.getUsername() + ": " + connection.getMetaData().getURL());

                    return connection;
                }
                catch (UnsupportedOperationException e)
                {
                    LOGGER.debug("User aware to connection is unsupperted. Use standard connection");
                    //Aktuální DataSource nepodporuje postrčení uživatele
                }
            }
        }

        Connection connection = getDataSource( database ).getConnection();
        LOGGER.debug("Get default connection " + ( database == null ? "" : database ) + ": " + connection.getMetaData().getURL());

        return connection;
    }

    @Override
    public <T> T execute(String database, SwitchDataSourceAction<T> action)
    {
        String oldDatabase = dataSourceName.get() ;
        EntityManagerHolder previous = null ;

        try
        {
            previous = connectSession(database);

            return action.execute();
        }
        finally
        {
            disconnectSession( previous ) ;
        }
    }

    /**
     * @return seznam dostupných datový zdrojů
     */
    public List<DataSourceDescriptor> getDataSources( boolean removeCurrent )
    {
        List<DataSourceDescriptor> result = new ArrayList<>( dataSourceDescriptors ) ;

        if ( removeCurrent )
        {
            result.remove(getCurrentDataSource());
        }

        return result ;
    }

    @Override
    public List<DataSourceDescriptor> getDataSources()
    {
        return getDataSources( false ) ;
    }

    /**
     * @return aktuální název datového zdroje
     */
    private String getCurrentDataSourceName()
    {
        String oldDatabase = dataSourceName.get() ;

        if ( StringHelper.isNull(oldDatabase) )
        {
            oldDatabase = getCurrentDataSourceNameFromUserDetails() ;
        }

        return oldDatabase ;
    }

    /**
     * @return název databáze ziskaného za aktuálně přihlášeného uživatele
     */
    protected abstract String getCurrentDataSourceNameFromUserDetails() ;

    @Override
    public DataSourceDescriptor getCurrentDataSource()
    {
        String oldDatabase = getCurrentDataSourceName() ;

        for( DataSourceDescriptor dsd : dataSourceDescriptors )
        {
            if ( StringHelper.isEquals(dsd.getName(), oldDatabase) )
            {
                return dsd ;
            }
        }

        return null ;
    }
}
