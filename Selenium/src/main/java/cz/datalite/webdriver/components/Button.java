package cz.datalite.webdriver.components;

import org.openqa.selenium.WebElement;

/**
 * Mirror of ZK Button
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Button extends ZkElement {

    public Button( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    public void click() {
        webElement.click();
        zkDriver.sleep(100);
        zkDriver.waitForProcessing();
    }
}
