package cz.datalite.cache;


import cz.datalite.cache.model.ServiceResult;

/**
 * Mezipamět pro ukládání vytvořených objektů
 */
@SuppressWarnings("unused")
public interface CacheService
{
    /**
     * @return true pokud je cache zapnutá
     */
    boolean isEnabled() ;

    /**
     * @param value nová hodnota příznaku zapnutí/vypnutí cache
     * @return true pokud byla cache zapnutá
     */
    boolean setEnabled( boolean value ) ;

    /**
     * Přidání záznamu do cache
     *
     * @param cacheType     typ pole
     * @param key           klíč
     * @param value         hodnota
     */
    <CacheType, XmlType, DatabaseType> void addToCache(Class<CacheType> cacheType, XmlType key, DatabaseType value) ;

    /**
     * Přidání záznamu do cache
     *
     * @param cacheType     typ pole
     * @param key           klíč
     * @param value         hodnota
     */
    <CacheType, XmlType, DatabaseType> DatabaseType removeFromCache(Class<CacheType> cacheType, XmlType key, DatabaseType value) ;

    /**
     * @param cacheType     typ pole
     * @param key           hledaný klíč
     * @return položka existuje
     */
    <CacheType, XmlType> boolean isExistsInCache(Class<CacheType> cacheType, XmlType key) ;

    /**
     * @param cacheType     typ pole
     * @param key           hledaný klíč
     * @param value         hodnotna
     * @return položka existuje
     */
    <CacheType, XmlType, DatabaseType> boolean isExistsInCache(Class<CacheType> cacheType, XmlType key, DatabaseType value) ;

    /**
     * @param cacheType     typ pole
     * @param key           hledaný klíč
     * @return hodnota z cache
     */
    <CacheType, XmlType, DatabaseType> DatabaseType getValueFromCache(Class<CacheType> cacheType, XmlType key) ;

    /**
     * @param regExpClassName     regularni vyraz pro určení plného názvu třídy
     * @param key                 hledaný klíč
     * @return hodnota z cache
     */
    <XmlType, DatabaseType> DatabaseType getValueFromCache( String regExpClassName, XmlType key) ;

    /**
     * Vymazání mezipaměti
     */
    void clear();

    /**
     * Přidání záznamu do cache
     *
     * @param key           klíč
     * @param value         hodnota
     */
    <XmlType> void addServiceResultToCache( XmlType key, ServiceResult value ) ;
}
