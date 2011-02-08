package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * ZK Element button.
 *
 * @author Jiri Bubnik
 */
public class ButtonElement extends ZkElement {

    public ButtonElement( final ZkDriver zkDriver, final ZkElement parent, final String zkId ) {
        super( zkDriver, parent, zkId );
        webElement = parent.findElement( By.id( getComponentIdPrefix() + zkId ) );
    }

    public ButtonElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }
}
