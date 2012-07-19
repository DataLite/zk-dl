package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Mirror of Checkbox
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Checkbox extends InputElement {

    public Checkbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement.findElement( By.tagName( "input" ) ) );
    }

    public void check() {
        clear();
        webElement.click();
    }

    public void uncheck() {
        webElement.clear();
    }

    public void toggle() {
        webElement.click();
    }

    @Override
    public void autoFill() {
        check();
    }

    @Override
    public String getValue() {
        return Boolean.toString( webElement.isSelected() );
    }

    @Override
    public void write( final String value ) {
        if ( "true".equalsIgnoreCase( value ) ) {
            check();
        } else if ( "false".equalsIgnoreCase( value ) ) {
            uncheck();
        } else {
            throw new UnsupportedOperationException( "Checkbox is not writable." );
        }
    }
}
