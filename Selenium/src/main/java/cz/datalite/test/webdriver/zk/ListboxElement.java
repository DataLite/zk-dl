package cz.datalite.test.webdriver.zk;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import cz.datalite.helpers.StringHelper;
import cz.datalite.webdriver.VisibilityOfElementLocated;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Jiri Bubnik
 */
public class ListboxElement extends ZkElement {

    private static final String TEXT_TO_NOT_FOUND = "\"@%Not _Found$'";
    /** Quick Filter (if available) */
    protected WebElement quickFilter;
    /** Listbox manager (if available) */
    protected WebElement listboxManager;

    public ListboxElement( final ZkDriver zkDriver, final ZkElement parent, final String zkId ) {
        super( zkDriver, parent, zkId );
        setupById();
    }

    public ListboxElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }

    /**
     * Setup WebElements of listbox. This method need to be called after filter / order by / manager
     * to ensure synchronization with elements.
     */
    public void setupById() {
        webElement = getWindow().findElement( By.id( getComponentIdPrefix() + zkId ) );

        final List<WebElement> quickFilters = getWindow().findElements( By.id( getComponentIdPrefix() + zkId + "_quickfilter" ) );
        quickFilter = quickFilters.isEmpty() ? null : quickFilters.get( 0 );

        //List<WebElement> listboxManagers = getParent().findElements(By.id(getComponentIdPrefix() + zkId + "_quickFilter"));
        //quickFilter = quickFilters.isEmpty() ? null : quickFilters.get(0);
    }

    /**
     * Find tbody element of listbox header.
     * @return listhead
     */
    protected WebElement findListhead() {
        for ( WebElement el : findElement( By.id( getComponentIdPrefix() + zkId + "-head" ) ).findElements( By.tagName( "tbody" ) ) ) {
            if ( el.getAttribute( "style" ) == null || !el.getAttribute( "style" ).contains( "hidden" ) ) {
                return el;
            }
        }
        throw new IllegalStateException( "Unexpected content of listbox header. There is no visible element tbody under element with id: " + zkId + "-head" );
    }

    /**
     * Finds a listheader by title.
     *
     * @param title the title
     * @return a listheader
     */
    public ListheaderElement findListheader( final String title ) {
        return new ListheaderElement( getZkDriver(), this, findListhead().findElement( By.xpath( "//th[@title='" + title + "']" ) ) );
    }

    /**
     * Finds all listheaders.
     *
     * @return listheaders
     */
    public List<ListheaderElement> findListheaders() {
        final List<ListheaderElement> headers = new LinkedList<ListheaderElement>();

        for ( WebElement el : findListhead().findElements( By.tagName( "th" ) ) ) {
            headers.add( new ListheaderElement( getZkDriver(), this, el ) );
        }

        return headers;
    }

    /**
     * Find the tbody element witch contains all table rows.
     *
     * @return the element
     */
    protected WebElement findRows() {
        return findElement( By.id( getComponentIdPrefix() + zkId + "-rows" ) );
    }

    /**
     * Returns element row count. Note that some elements may not be loaded yet, but are included in the count.
     * 
     * @return row count
     */
    public int rowCount() {
        return findRows().findElements( By.tagName( "tr" ) ).size();
    }

    /**
     * Check if the listbox is empty. This method only finds first tr tag - so it is much faster then rowCount() > 0.
     *
     * @return true, if no rows are included
     */
    public boolean isEmpty() {
        try {
            findRows().findElement( By.tagName( "tr" ) );
            return true;
        } catch ( ElementNotFoundException e ) {
            return false;
        }
    }

    /**
     * Find element at row.
     *
     * @param row row number
     * @return listitem element
     */
    public ListitemElement findListitem( final int row ) {
        return new ListitemElement( getZkDriver(), this, findRows().findElements( By.tagName( "tr" ) ).get( row ) );
    }

    /**
     * Setup quick filter for property.
     *
     * @param property name of the property
     */
    public void doQuickFilterSetup( final String property ) {
        if ( quickFilter == null ) {
            throw new ElementNotFoundException( "Quick filter element not found in listbox " + getZkId(), null, null );
        }

        quickFilter.findElement( By.tagName( "input" ) ).click(); // move focus first
        quickFilter.findElement( By.className( "datalite-listbox-qfiltr-open" ) ).click();
        getZkDriver().waitForProcessing();

        // popup is not in DOM tree, should be open seperately
        final WebElement popup = getZkDriver().findElement( By.className( "datalite-listbox-qfiltr-popup" ) );

        // partial is not quite ok, but link text contains some span as well...
        final By propertyBy = By.partialLinkText( property );
        getZkDriver().until( new VisibilityOfElementLocated( popup, propertyBy ) );
        popup.findElement( propertyBy ).click();

        getZkDriver().waitForProcessing();
    }

    /**
     * Type text into quick filter field and submit.
     *
     * @param filter
     */
    public void doQuickFilter( final String filter ) {
        if ( quickFilter == null ) {
            throw new ElementNotFoundException( "Quick filter element not found in listbox " + getZkId(), null, null );
        }

        final WebElement input = quickFilter.findElement( By.tagName( "input" ) );
        input.click();
        input.clear();
        input.sendKeys( filter );

        quickFilter.findElement( By.className( "datalite-listbox-qfiltr-image" ) ).click();

        // wait and reload
        getZkDriver().waitForProcessing();
        setupById();
    }

    public void testAll() {
        if ( !getZkDriver().isFullTest() ) {
            return;
        }

        // click on all listheaders
        final int size = findListheaders().size();
        for ( int i = 0; i < size; i++ ) {
            setupById(); // reload listbox by dom actual content

            final ListheaderElement el = findListheaders().get( i );
            el.click();

            getZkDriver().waitForProcessing();
        }


        // search with all quick Filter
        if ( quickFilter != null ) {
            // open quick filtr popup
            quickFilter.findElement( By.className( "datalite-listbox-qfiltr-open" ) ).click();
            getZkDriver().waitForProcessing();

            // find all columns
            final WebElement popup = getZkDriver().findElement( By.className( "datalite-listbox-qfiltr-popup" ) );
            final List<String> quickFilterColumns = new LinkedList<String>();
            for ( WebElement column : popup.findElements( By.tagName( "a" ) ) ) {
                if ( !StringHelper.isNull( column.getText() ) ) {
                    quickFilterColumns.add( column.getText() );
                }
            }

            for ( String column : quickFilterColumns ) {
                doQuickFilterSetup( column );
                doQuickFilter( TEXT_TO_NOT_FOUND );
                // check no data return
            }
        }

    }
}
