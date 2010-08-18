package cz.datalite.zk.help;

/**
 * Interface pro rozšíření ZK komponent o internacionalizaci a automatickou nápovědu z databáze.
 * 
 * Vlastnost
 * Používá se pouze, pokud jsou dvě různé komponenty pod stejným identifikátorem
 * (value, label).
 * Nastavuje se jako help="novyIdentifikatorNapovedy"
 * @author Michal Pavlusek
 */
public interface DLI18n
{
    /**
     * Setup  id to find help
     * @param helpId
     */
    public void setHelpId(String helpId);

    /**
     * Returns id of help.
     * @return the id
     */
    public String getHelpId();

    /**
     * Funkce nastavuje , ktery se ma zobrazit v napovede
     *
     * @param helpText
     */
    public void getHelpText(String helpText);

    /**
     * Funkce vrací text, ktery se ma zobrazit v napovede
     * @return the text
     */
    public String getHelpText();


    /**
     * Funkce nastavuje identifikátor pro property file s lokalizaci
     * @param helpId the helpId
     */
    public void setLanguageId(String helpId);

    /**
     * Funkce vrací identifikátor pro property file s lokalizaci
     * @return the id
     */
    public String getLanguageId();

}
