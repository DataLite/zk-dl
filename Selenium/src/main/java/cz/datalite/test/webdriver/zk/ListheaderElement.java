package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 *
 * @author Jiri Bubnik
 */
public class ListheaderElement extends ZkElement {

    public ListheaderElement( final ZkDriver zkDriver, final ZkElement parent, final String zkId ) {
        super( zkDriver, parent, zkId );
        webElement = parent.findElement( By.id( getComponentIdPrefix() + zkId ) );
    }

    public ListheaderElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }
}
