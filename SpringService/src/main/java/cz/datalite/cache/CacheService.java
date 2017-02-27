package cz.datalite.cache;

import cz.datalite.cache.model.ServiceResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @return položka existuje
     */
    <CacheType> boolean isExistsInCache(Class<CacheType> cacheType);

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

    /**
     * Vrati vsechny objekty danych trid.
     * @param cacheTypes pozadovane tridy
     * @return seznam objektu danych trid
     */
    List<?> getAllValues(Collection<Class<?>> cacheTypes);

    /**
     * Vrati mapu objektu dane tridy.
     * @param cacheType trida objektu
     * @return immutable mapa klic->objekt
     */
    <CacheType> Map getAllValues(Class<CacheType> cacheType);

    /**
     * Vrati vchny tridy v cachi.
     * @return immutable mnozina trid objektu z cache
     */
    Set<Class<?>> getAllClasses();
}
