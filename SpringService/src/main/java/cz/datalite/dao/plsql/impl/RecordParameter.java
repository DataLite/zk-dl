package cz.datalite.dao.plsql.impl;


/**
 * Speciální vstupní parametr (PL/SQL record)
 */
interface RecordParameter<T>
{
    /**
     * @return cilová entita
     */
    Class<T> getTargetEntity() ;

    /**
     * @return jméno proměnné
     */
    String getVariableName() ;

    /**
     * @return databázový typ
     */
    String getDatabaseType() ;

    /**
     * @return jméno parametru
     */
    String getName() ;

    /**
     * @return true pokud se jedná o vstupní parametr
     */
    boolean isInput() ;

    /**
     * @return true pokud se jedná o výstupní parametr
     */
    boolean isOutput() ;

    /**
     * @return true pokud se jedná o pole
     */
    boolean isArray() ;
}
