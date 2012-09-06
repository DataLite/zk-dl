package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Jiri Bubnik
 */
public class ListcellElement extends ZkElement {

    public ListcellElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }

    /**
     * Returns label of typical listcell (in html it is realized as td with one div).
     *
     * @return label of the listcell
     */
    public String getLabel() {
        return findElement( By.tagName( "div" ) ).getText();
    }
}
