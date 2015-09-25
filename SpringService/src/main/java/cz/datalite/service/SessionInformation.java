package cz.datalite.service;

/**
 * Služba pro získání informace o aktuální databazových operací
 */
@SuppressWarnings("UnusedDeclaration")
public interface SessionInformation
{
    /**
     * @param id        identifikátor sezení
     * @return popis sezeni
     */
    String getSessionInformation(String id) ;

    /**
     * @return  aktuální identifikátor sezení
     */
    String getCurrentSessionId() ;

    /**
     * @param clientInfo        informace o sezení
     */
    void setSessionInformation(String clientInfo) ;

    /**
     * Zrušení informací o sezení
     */
    void clearSessionInformation() ;

    /**
     * @param sessionId        identifikátor sezení
     * @param clientInfo       informace o sezeni
     */
    void setSessionInformation(String sessionId, String clientInfo) ;

    /**
     * Nastaveni identifikace sezeni
     * @param id  identifikace sezeni
     */
    void setCurrentSessionId(String id) ;
}
