package cz.datalite.zk.components.button;

import cz.datalite.zk.help.DLI18n;
import java.io.IOException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Button;

/**
 * ZK Button extensions.
 *
 * List of extensions:
 * <ul>
 *   <li>i18n</li>
 *   <li>readonly - depends on disabledOnReadonly disable the button if ZKHelper.setReadonly() is set</li>
 *   <li>load image from classpath</li>
 * </ul>
 *
 * @author Jiri Bubnik
 */
public class DLButton extends Button implements DLI18n {

    /**
     * Arbitrary value associated to the button.
     */
    private Object value;

    /**
     * Disabled as set by the user. Parent disabled can be set according to value and disabledOnNullValue.
     */
    private boolean userDisabled;

    /**
     * Disable the button if no value data is associated.
     *
     */
    private boolean disabledOnNullValue;

    /**
     * If you set readonly for whole page (or component tree) via ZKHelper.setReadonly(), this will check if button has to be disabled as well.
     */
    private boolean disabledOnReadonly;

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

    public boolean isDisabledOnNullValue() {
        return disabledOnNullValue;
    }

    public void setDisabledOnNullValue(boolean disabledOnNullValue) {
        this.disabledOnNullValue = disabledOnNullValue;
    }

    /**
     * Arbitrary value associated to the button.
     *
     * @return Arbitrary value associated to the button.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Arbitrary value associated to the button.
     * If the value is null/not null and isDisabledOnNullValue is true, then the button will be disabled/enabled
     * (enabled only if setDisabled(true) was not set).
     *
     * @param value Arbitrary value associated to the button.
     */
    public void setValue(Object value) {
        this.value = value;

        if (isDisabledOnNullValue() && !userDisabled)
        {
            super.setDisabled(value == null);
        }
    }


    /**
     * Like button setDisabled, only it checks value and disabledOnNullValue params as well.
     *
     * @param disabled should be the button enabled or disabled
     */
    @Override
    public void setDisabled(boolean disabled)
    {
        this.userDisabled = disabled;

        if (isDisabledOnNullValue() && !disabled)
        {
            // enable only if value is set
            if (value != null)
                super.setDisabled(disabled);
        }
        else
        {
            super.setDisabled(disabled);
        }
    }


    /**
     * If you set readonly for whole page (or component tree) via ZKHelper.setReadonly(), this will check if button has to be disabled as well.
     * @return the disabledOnReadonly
     */
    public boolean isDisabledOnReadonly() {
        return disabledOnReadonly;
    }

    /**
     * If you set readonly for whole page (or component tree) via ZKHelper.setReadonly(), this will check if button has to be disabled as well.
     * @param disabledOnReadonly the disabledOnReadonly to set
     */
    public void setDisabledOnReadonly( boolean disabledOnReadonly ) {
        this.disabledOnReadonly = disabledOnReadonly;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void setHelpId( String helpId ) {
        this.helpId = helpId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void getHelpText( String helpText ) {
        this.helpText = helpText;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setLanguageId( String languageId ) {
        this.languageId = languageId;
    }

    public String getLanguageId() {
        return languageId;
    }

}
