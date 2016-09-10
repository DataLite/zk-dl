package cz.datalite.service;

/**
 * Operace, která se má provést po vyzvednutí spojení do DB
 */
@SuppressWarnings("unused")
public interface ConnectionInterceptorWithSemafore extends ConnectionInterceptor
{
    /**
     * @param value  hodnota příznaku globálního zapnutí/vypnutí
     */
    void setEnableReset(boolean value ) ;

    /**
     * @return hodnota příznaku globálního zapnutí/vypnutí
     */
    boolean isEnableReset() ;
}
