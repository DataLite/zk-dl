package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;

/**
 *
 * Mirror component of ZK Lovbox. This component can fill it, read some
 * properties, manage filter and paging. This component is part of form
 * components which means that there is define autofill method which can
 * be called fo automatical form fill. The autofilled value is first row.
 * If there are no records then no value is selected.
 *
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class Lovbox extends InputElement {

    protected WebElement popup;
    protected Listbox listbox;
    protected QuickFilter filter;

    public Lovbox( final ZkElement parent, final WebElement webElement ) {
        super( parent, webElement );
    }

    /**
     * @see QuickFilter#set(java.lang.String, java.lang.String)
     */
    public void setQuickFilter( final String key, final String value ) {
        open();
        getQuickFilter().set( key, value );
    }

    /**
     * @see QuickFilter#setKey(java.lang.String)
     */
    public void setQuickFilterKey( final String key ) {
        open();
        getQuickFilter().setKey( key );
    }

    /**
     * @see QuickFilter#setKey(int)
     */
    public void setQuickFilterKey( final int index ) {
        open();
        getQuickFilter().setKey( index );
    }

    /**
     * @see QuickFilter#set(java.lang.String)
     */
    public void setQuickFilter( final String value ) {
        open();
        getQuickFilter().set( value );
    }

    /**
     * @see QuickFilter#clear()
     */
    public void clearQuickFilter() {
        open();
        getQuickFilter().clear();
    }

    /**
     * @see Paging#nextPage() 
     */
    public void nextPage() {
        open();
        getPaging().nextPage();
    }

    /**
     * @see Paging#previousPage()
     */
    public void previousPage() {
        open();
        getPaging().previousPage();
    }

    /**
     * @see Paging#goToPage(int)
     */
    public void goToPage( final int page ) {
        open();
        getPaging().goToPage( page );
    }

    /**
     * @see Paging#getPageCount()
     */
    public int getPageCount() {
        open();
        return getPaging().getPageCount();
    }

    /**
     * Returs count of rows actually shown in listbox - part of lovbox
     * @return count of shown rows
     */
    public int getRowsCount() {
        open();
        return getListbox().getRowCount();
    }

    /**
     * Selects row on given index as selected. This index starts from 1
     * @param index index of selected row, starts from 1
     */
    public void selectRow( final int index ) {
        open();
        getListbox().selectRow( index );
        close();
    }

    /**
     * Opens lovbox popup if is not opened
     */
    public void open() {
        if ( isOpened() ) {
            return;
        }
        final WebElement btn = webElement.findElement( By.className( "z-bandbox-btn" ) );
        btn.click();
        zkDriver.waitForProcessing();

        int i = 0;
        while ( !isOpened() ) {
            if ( i++ >= 5 ) {
                throw new IllegalStateException( "Lovbox couldn't be opened." );
            }
            btn.click();
            zkDriver.waitForProcessing();
        }
    }

    /**
     * Closes lovbox popup if is opened
     */
    public void close() {
        if ( isOpened() ) {
            final WebElement btn = webElement.findElement( By.className( "z-bandbox-btn" ) );
            btn.click();
            zkDriver.waitForProcessing();
        }
    }

    public void autoFill() {
        if ( webElement.findElement( By.tagName( "input" ) ).isEnabled() ) {
            selectRow( 1 );
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException( "Lovbox is not clearable." );
    }

    @Override
    public void write( final String value ) {
        throw new UnsupportedOperationException( "Lovbox is not writeable." );
    }

    @Override
    public String getValue() {
        return webElement.findElement( By.tagName( "input" ) ).getText();
    }

    protected boolean isOpened() {
        try {
            return (( RenderedWebElement ) getPopup()).isDisplayed();
        } catch ( NoSuchElementException ex ) {
            return false;
        }
    }

    protected WebElement getPopup() {
        return popup == null ? popup = ZkElement.findElement( By.id( webElement.getAttribute( "id" ) + "-pp" ) ) : popup;
    }

    protected QuickFilter getQuickFilter() {
        return filter == null ? filter = new QuickFilter( this, getPopup().findElement( By.className( "z-hbox" ) ) ) : filter;
    }

    protected Paging getPaging() {
        return getListbox().getPaging();
    }

    protected Listbox getListbox() {
        if ( listbox == null ) {
            listbox = new Listbox( this, getPopup().findElement( By.className( "z-listbox" ) ) );
            listbox.setPaging( new Paging( this, getPopup().findElement( By.className( "z-paging" ) ) ) );
        }
        return listbox;
    }
}
