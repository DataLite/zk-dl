package cz.datalite.service.impl;

import cz.datalite.service.ConnectionInterceptor;
import cz.datalite.service.InterceptorDataSourceSwitcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.sql.Connection;
import java.util.*;

@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"})
public abstract class AbstractInterceptorDataSourceSwitcherService extends AbstractDataSourceSwitcherService implements InterceptorDataSourceSwitcherService
{
    /**
     * Seznam tříd definujicí operace, které se mají provést po vyzvednutí spojení do DB
     */
    private List<Class<? extends ConnectionInterceptor>> connectionInterceptorClasses = new LinkedList<>() ;

    /**
     * Seznam operace, které se mají provést po vyzvednutí spojení do DB
     */
    private List<ConnectionInterceptor> connectionInterceptors = new LinkedList<>() ;

    /**
     * Příznak zda jsou povoleny intercetori
     */
    private boolean enabled = true ;

    /**
     * Seznam tříd definujicí operace, které se mají provést po vyzvednutí spojení do DB
     */
    private Set<Class<? extends ConnectionInterceptor>> disabled = new HashSet<>() ;

    /**
     * @param userDetailsServiceName Název třídy pro získání uživatele
     */
    public AbstractInterceptorDataSourceSwitcherService(String userDetailsServiceName)
    {
        super( userDetailsServiceName ) ;
    }

    /**
     * @param userDetailsService  implementace služby pro získání uživatele
     */
    public AbstractInterceptorDataSourceSwitcherService(UserDetailsService userDetailsService)
    {
        super( userDetailsService ) ;
    }

    @Override
    public void setConnectionInterceptorClasses(List<Class<? extends ConnectionInterceptor>> connectionInterceptorClasses)
    {
        if ( connectionInterceptorClasses != null )
        {
            this.connectionInterceptorClasses.clear() ;
            this.connectionInterceptorClasses.addAll(connectionInterceptorClasses);
        }
    }

    @Override
    public void setConnectionInterceptors(List<ConnectionInterceptor> connectionInterceptors)
    {
        if ( connectionInterceptors != null )
        {
            this.connectionInterceptors.clear() ;
            this.connectionInterceptors.addAll(connectionInterceptors);
        }
    }

    /**
     * Operace, které se provedou po vyzvednutí spojení
     *
     * @param connection        aktuální spojení
     */
    protected void afterGetConnection( Connection connection )
    {
        if ( ! isEnableInterceptors( connection ) )
        {
            return ;
        }

        for( ConnectionInterceptor ci : connectionInterceptors )
        {
            if ( ! disabled.contains( ci.getClass() ) )
            {
                ci.onConnection(connection);
            }
        }

        if ( applicationContext != null )
        {
            ListIterator<Class<? extends ConnectionInterceptor>> it = connectionInterceptorClasses.listIterator();

            while (it.hasNext())
            {
                Class<? extends ConnectionInterceptor> next = it.next();

                if ( (next != null) && ( ! disabled.contains( next ) ) )
                {
                    ConnectionInterceptor ci = applicationContext.getBean(next) ;

                    connectionInterceptors.add( ci ) ;
                    it.remove() ;

                    ci.onConnection( connection ) ;
                }
            }
        }
    }

    /**
     * @return true povoleny interceptori
     */
    protected boolean isEnableInterceptors( Connection connection )
    {
        return enabled ;
    }

    @Override
    public void disableInterceptors()
    {
        this.enabled = false ;
    }

    @Override
    public void enableInterceptors()
    {
        this.enabled = true ;
    }

    @Override
    public <Type extends ConnectionInterceptor> void disableInterceptor(Class<Type> type)
    {
        this.disabled.add( type ) ;
    }

    @Override
    public <Type extends ConnectionInterceptor> void enableInterceptors(Class<Type> type)
    {
        this.disabled.remove( type ) ;
    }
}
