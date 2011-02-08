package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * This parent class defines that the component can be part of form.
 * It means that there are methods for filling, clearing and getting values.
 * These components can be filled by special methods or by autofill. Autofill
 * writes to the component some constant valid data.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public abstract class InputElement extends ZkElement {

    public InputElement( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    /**
     * <p>The component is filled with some valid data which means
     * allows to form to autofill all components. </p>
     *
     *
     * <p>Every component has different type of data as valid
     * and some components has to do a selection from the list.
     * Due to very different implementations this is abstract method.
     * There is no general solution.</p>
     */
    public abstract void autoFill();

    /**
     * Sends keys to the component. This is usable only on components
     * which can be filled from keyboard.
     * @param value string which is written to the component
     */
    public void write( final String value ) {
        clear();
        webElement.sendKeys( value );
    }

    /**
     * Clears the value in the component
     */
    public void clear() {
        webElement.clear();
    }

    /**
     * Returns value written or selected in the component
     * @return value of component
     */
    public String getValue() {
        return webElement.getText();
    }
}
