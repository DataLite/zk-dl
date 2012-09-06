package cz.datalite.zk.components.tabbox;

import cz.datalite.zk.help.DLI18n;
import org.zkoss.zul.Tab;

/**
 * Rozsireni ZK komponenty tab
 *
 * @author Jiri Bubnik
 */
public class DLTab extends Tab implements DLI18n
{
    /**
     * Identifikator pro nalezeni helpu v databazi
     */
    private String helpId;

    /**
     * Text pro zobrazeni helpu (popup okno)
     */
    private String helpText;

    /**
     * Identifikator pro internacionalizaci (property file)
     */
    private String languageId;


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void getHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageId() {
        return languageId;
    }
}
