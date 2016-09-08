package cz.datalite.service;

/**
 * Operace, která se má provést po vyzvednutí spojení do DB
 */
public interface ConnectionInterceptorWithSemafore extends ConnectionInterceptor
{
    /**
     * @param value  hodnota příznaku globálního zapnutí/vypnutí
     */
    void setGlobal( boolean value ) ;
}
