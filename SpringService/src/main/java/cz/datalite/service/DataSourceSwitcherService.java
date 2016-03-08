package cz.datalite.service;


import cz.datalite.model.DataSourceDescriptor;

import java.util.List;

/**
 * Služba pro přepínání datových zdrojů
 */
@SuppressWarnings("UnusedDeclaration")
public interface DataSourceSwitcherService
{
    /**
     * @param name      testovaný název datového zdroje
     * @return true datový zdroj je k dispozici
     */
    boolean isAvailable(String name) ;

    /**
     * Spuštění transakční operace
     *
     * @param action                        spouštěný příkaz
     * @return výsledek
     */
    <T> T execute(String database, SwitchDataSourceAction<T> action) ;

    /**
     * @return seznam dostupných datový zdrojů
     */
    List<DataSourceDescriptor> getDataSources() ;

    /**
     * @return aktuální datový zdroj
     */
    DataSourceDescriptor getCurrentDataSource() ;


    /**
     * @param removeCurrent     příznak zda ze seznamu odebrat aktuální databázi
     * @return seznam dostupných datový zdrojů
     */
    List<DataSourceDescriptor> getDataSources(boolean removeCurrent) ;
}
