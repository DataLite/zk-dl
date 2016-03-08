package cz.datalite.service;


import java.util.List;

/**
 * Služba pro přepínání datových zdrojů
 */
@SuppressWarnings("UnusedDeclaration")
public interface InterceptorDataSourceSwitcherService extends DataSourceSwitcherService
{
    /**
     * Zakázání všech interceptorů
     */
    void disableInterceptors() ;

    /**
     * Povolení všech interceptorů
     */
    void enableInterceptors() ;

    /**
     * Zakázání interceptorů
     *
     * @param type      typ zakazovanách interceptorů
     */
    <Type extends ConnectionInterceptor> void disableInterceptor(Class<Type> type) ;

    /**
     * Zakázání interceptorů
     *
     * @param type      typ povolovaných interceptorů
     */
    <Type extends ConnectionInterceptor> void enableInterceptors(Class<Type> type) ;

    /**
     * @param connectionInterceptorClasses Seznam tříd definujicí operace, které se mají provést po vyzvednutí spojení do DB
     */
    void setConnectionInterceptorClasses(List<Class<? extends ConnectionInterceptor>> connectionInterceptorClasses) ;

    /**
     * @param connectionInterceptors seznam operace, které se mají provést po vyzvednutí spojení do DB
     */
    void setConnectionInterceptors(List<ConnectionInterceptor> connectionInterceptors) ;
}
