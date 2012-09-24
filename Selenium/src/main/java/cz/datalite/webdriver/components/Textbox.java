package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * This component is the mirror of ZK Textbox element.
 * 
 * @author Karel Cemus
 */
public class Textbox extends InputElement {

    public Textbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    public void autoFill() {
        write( "Lorem ipsum dolor" );
    }
}
