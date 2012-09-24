package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * Mirror of Doublebox
 * @author Karel Cemus
 */
public class Doublebox extends InputElement {

    public Doublebox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public void autoFill() {
        write( "123,45" );
    }
}
