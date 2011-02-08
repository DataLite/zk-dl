package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Window element is the main component of each page. This is the most common
 * parent of other components and is used as base line in unit test.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Window extends ZkElement {

    public Window( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    @Override
    public Window getWindow() {
        return this;
    }

    /**
     * Supposes that the window is closable and clicks on its cross button.
     * If the button doesn't exists the the exceptin is thrown. 
     */
    public void close() {
        final WebElement closeBtn = webElement.findElement( By.id( webElement.getAttribute( "id" ) + "-close" ) );
        closeBtn.click();
    }
}
