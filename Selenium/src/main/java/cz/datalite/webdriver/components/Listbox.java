package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Mirror of ZK Listbox component. This also covers QuickFilter and Paging.
 * This component is created from WebElement with class style z-listbox and
 * using back-doors there is sent query to server to get paging and 
 * query uuids.
 *
 * @author Karel Cemus
 */
public class Listbox extends ZkElement {

    enum Phase {

        /** ascending */
        ASC(),
        /** descending */
        DESC(),
        /** natural - non sorted */
        NATURAL();
    }
    /** paging component */
    protected Paging paging;
    /** quickfilter component */
    protected QuickFilter quickFilter;
    /** listbox component */
    protected WebElement parentElement;

    public Listbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
        parentElement = webElement.findElement( By.xpath( ".//parent::*" ) );
        final String[] uuids = (zkDriver.sendCommand( "listboxComponents", webElement.getAttribute( "id" ) ) + ", ").split( "," );
        if ( uuids[1] != null && !"".equals( uuids[1] ) ) {
            paging = new Paging( this, findElement( By.id( uuids[1] ) ) );
        }
        if ( uuids[2] != null && !"".equals( uuids[2] ) ) {
            quickFilter = new QuickFilter( this, findElement( By.id( uuids[2] ) ) );
        }

    }

    /**
     * Executes full test of listheaders. It means that every lishteader goes
     * through all phases of sorting. This is slow part of test so it should
     * be executed only as part of full test which is executed by testing
     * server.
     */
    public void testListheaders() {
        final int count = parentElement.findElement( By.className( "z-listhead" ) ).findElements( By.className( "z-listheader" ) ).size();
        for ( int index = 0; index < count; ++index ) {
            testListheader( index );
        }
        webElement = parentElement.findElement( By.className( "z-listbox" ) );
    }

    /**
     * Executes full test on single listheader
     * @param index index of listheader from 0
     */
    protected void testListheader( final int index ) {
        for ( Phase phase : Phase.values() ) {
            // StaleElementReferenceException can be thrown if javascript object is not yet ready (attached to the DOM).
            int recoveryCount = 10;
            while(recoveryCount > 0)
            {
                try{
                    final List<WebElement> listheaders = parentElement.findElement( By.className( "z-listhead" ) ).findElements( By.className( "z-listheader" ) );
                    sortListheader( listheaders.get( index ) );
                    break;
                }
                catch (StaleElementReferenceException e) { }
                recoveryCount--;
                zkDriver.sleep(100);
            }

            if (recoveryCount == 0)
                throw new StaleElementReferenceException("Listheader " + index + " shows StaleElementReferenceException even after 10 retry.");
        }
    }

    protected void sortListheader( final WebElement listheader ) {
        int recoveryCount = 10;
        while(recoveryCount > 0)
        {
            try
            {
                listheader.click();
                break;
            }
            catch (ElementNotVisibleException e) {}

            recoveryCount--;
            zkDriver.sleep(100);
        }

        if (recoveryCount == 0)
            throw new ElementNotVisibleException("Listheader " + listheader + " not visible even after 10 retry.");

        zkDriver.waitForProcessing();
    }

    public List<WebElement> getRows() {
        return webElement.findElements( By.className( "z-listitem" ) );
    }

    public int getRowCount() {
        return getRows().size();
    }

    public void selectRow( final int index ) {
        getRows().get( index - 1 ).click();
        zkDriver.waitForProcessing();
    }

    public void selectFirstRow() {
        getRows().get( 1 ).click();
    }

    public void selectLastRow() {
        final List<WebElement> rows = getRows();
        rows.get( rows.size() - 1 ).click();
    }

    public WebElement getRow( final int index ) {
        return getRows().get( index - 1 );
    }

    public WebElement getCell(  final int row, final int column ) {
        final WebElement rowEl = getRow( row );
        return rowEl.findElements( By.xpath( "./td" ) ).get( column - 1 );
    }

    public String get( final int row, final int column ) {
        return getCell(row, column).getText();
    }

    public int getPageCount() {
        return getPaging().getPageCount();
    }

    public int getPageIndex() {
        return getPaging().getPageIndex();
    }

    public void goToPage( final int index ) {
        getPaging().goToPage( index );
    }

    public void nextPage() {
        getPaging().nextPage();
    }

    public void previousPage() {
        getPaging().previousPage();
    }

    public void doubleClickOnFirstRow() {
        selectFirstRow();
        doubleClickOnRow( 1 );
    }

    public void doubleClickOnRow( final int index ) {
        selectRow( index );
        doubleClickOn( getCell( index, 1) );
    }

    public void setPaging( final Paging paging ) {
        this.paging = paging;
    }

    public Paging getPaging() {
        return paging;
    }

    public boolean isPaging() {
        return paging != null;
    }

    public boolean isQuickFilter() {
        return quickFilter != null;
    }

    public void setQuickFilter( final String key, final String value ) {
        getQuickFilter().set( key, value );
    }

    public void setQuickFilterKey( final String key ) {
        getQuickFilter().setKey( key );
    }

    public void setQuickFilterKey( final int index ) {
        getQuickFilter().setKey( index );
    }

    public void setQuickFilter( final String value ) {
        getQuickFilter().set( value );
    }

    public void clearQuickFilter() {
        getQuickFilter().clear();
    }

    protected QuickFilter getQuickFilter() {
        return quickFilter;
    }
}
