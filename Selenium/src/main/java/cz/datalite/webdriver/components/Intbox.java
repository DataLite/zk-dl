package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * Mirror of ZK Intbox component
 * 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Intbox extends InputElement {

    public Intbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public void autoFill() {
        write( "12345" );
    }
}
