package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jiri Bubnik
 */
public class ListitemElement extends ZkElement {

    public ListitemElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }

    /**
     * Find a listcell.
     *
     * @param column column index
     * @return the listcell
     */
    public ListcellElement findListcell( final int column ) {
        return new ListcellElement( getZkDriver(), this, findElements( By.tagName( "td" ) ).get( column ) );
    }

    /**
     * Get all listcells of listrow. It inicialize all objects, so if you need only one cell, use findListcell.
     *
     * @return listcells
     */
    public List<ListcellElement> findListcells() {
        final List<ListcellElement> cells = new LinkedList<>();

        for ( WebElement el : findElements( By.tagName( "td" ) ) ) {
            cells.add( new ListcellElement( getZkDriver(), this, el ) );
        }

        return cells;
    }

    /**
     * Simulates double click by click() and click()
     */
    public void doubleClick() {
        click();
        click();
    }
}
