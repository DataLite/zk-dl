package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Calendar;

/**
 * Mirror of Datebox
 *
 * @author Karel Cemus
 */
public class Datebox extends InputElement {

    public Datebox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public void autoFill() {
        write( "1.1." + Calendar.getInstance().get( Calendar.YEAR ) );
    }

    @Override
    public void write( final String value ) {
        clear();
        webElement.findElement( By.tagName( "input" ) ).sendKeys( value );
    }

    @Override
    public void clear() {
        webElement.findElement( By.tagName( "input" ) ).clear();
    }

    @Override
    public String getValue() {
        return webElement.findElement( By.tagName( "input" ) ).getAttribute("value");
    }
}
