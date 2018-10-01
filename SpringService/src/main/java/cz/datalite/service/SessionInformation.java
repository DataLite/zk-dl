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

    /**
     * Nastavení informací s časem zpracování
     *
     * @param sessionId        identifikátor sezení
     * @param format           format zpravy ( %C - aktuální pořadí operace, %S - celkový počet operací, %P - aktuální pořadí operace v procentech, %D - nahrazeno aktuální délkou zpracování, %T - předpokádanou délkou zpracování, %s nahrazeno argumentama )
     * @param current          aktuální pořadí operace
     * @param total            celkový počet operací
     * @param args             argumenty zpravy
     */
    void setSessionInformationWithTime( String sessionId, String format, int current, int total, String ... args ) ;

    /**
     * Nastavení informací s časem zpracování
     *
     * @param format           format zpravy ( %C - aktuální pořadí operace, %S - celkový počet operací, %P - aktuální pořadí operace v procentech, %D - nahrazeno aktuální délkou zpracování, %T - předpokádanou délkou zpracování, %s nahrazeno argumentama )
     * @param current          aktuální pořadí operace
     * @param total            celkový počet operací
     * @param args             argumenty zpravy
     */
    void setSessionInformationWithTime( String format, int current, int total, String ... args ) ;
}
