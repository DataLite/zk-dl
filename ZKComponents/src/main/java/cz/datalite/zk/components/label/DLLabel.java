package cz.datalite.zk.components.label;

import cz.datalite.zk.help.DLI18n;
import org.zkoss.zul.Label;

/**
 * ZK Label extensions.
 *
 * List of extensions:
 * <ul>
 *   <li>i18n</li>
 * </ul>
 * 
 * @author Jiri Bubnik
 */
public class DLLabel extends Label implements DLI18n
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

    /**
     * Default constructor
     */
    public DLLabel() {
        super();
    }

    /**
     * Constructor with label
     * @param name
     */
    public DLLabel(String name) {
        super();
        setValue(name);
    }

    
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
