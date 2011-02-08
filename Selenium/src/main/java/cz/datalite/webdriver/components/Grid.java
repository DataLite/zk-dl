package cz.datalite.webdriver.components;

import cz.datalite.webdriver.By;
import java.util.List;
import org.openqa.selenium.WebElement;

/**
 * Mirror of Grid
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Grid extends ZkElement {

    protected List<WebElement> rows;

    public Grid( final ZkElement parent, final WebElement element ) {
        super( parent, element );
    }

    /**
     * Returns all rows
     * @return rows
     */
    public List<WebElement> getRows() {
        if ( rows == null ) {
            rows = webElement.findElements( By.className( "z-row" ) );
        }
        return rows;
    }

    /**
     * Returns count of rows
     * @return rows count
     */
    public int getRowCount() {
        return getRows().size();
    }

    /**
     * Returns row with given index
     * @param index row index - starts from 1
     * @return Row
     */
    public ZkElement getRow( final int index ) {
        return new ZkElement( this, getRows().get( index - 1 ) );
    }

    /**
     * Returns WebElement of Grid on this position.
     * @param row row index starts from 1
     * @param column column index starts from 1
     * @return WebElement on this position
     */
    public ZkElement get( final int row, final int column ) {
        final WebElement rowEl = getRows().get( row - 1 );
        return new ZkElement( this, rowEl.findElements( By.className( "z-row-inner" ) ).get( column - 1 ) );
    }
}
