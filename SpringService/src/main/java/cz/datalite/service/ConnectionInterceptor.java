package cz.datalite.service;

import java.sql.Connection;

/**
 * Operace, která se má provést po vyzvednutí spojení do DB
 */
public interface ConnectionInterceptor
{
    /**
          * @param connection  aktuální spojení
     */
    void onConnection( Connection connection ) ;
}
