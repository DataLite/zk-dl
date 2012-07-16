package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * Mirror of Zk Decimalbox
 * 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Decimalbox extends InputElement {

    public Decimalbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public void autoFill() {
        write( "12345" );
    }

    @Override
    public String getValue() {
        return webElement.getAttribute("value");
    }
}
