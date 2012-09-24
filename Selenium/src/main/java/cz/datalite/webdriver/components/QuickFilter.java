package cz.datalite.webdriver.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * <p>This component is mirror of DLZK component - QuickFilter. It allows to 
 * fill it, to clear it etc. This is usuallly bind to other component.</p>
 *
 * @author Karel Cemus
 */
public class QuickFilter extends ZkElement {

    QuickFilter( final ZkElement parent, final WebElement element ) {
        super( parent, element );
    }

    /**
     * Sets value and key in quick filter. After set is automatically
     * called filter.
     * @param key key in the menu - case insensitive
     * @param value value in quick filter textbox
     */
    public void set( final String key, final String value ) {
        setKey( key );
        set( value );
    }

    /**
     * Sets value of quick filter. After set is automatically called filter.
     * After set is automatically called filter.
     * @param value filtering value
     */
    public void set( final String value ) {
        final WebElement input = webElement.findElement( By.tagName( "input" ) );
        input.clear();
        input.sendKeys( value );
        input.sendKeys(Keys.ENTER);
        filter();
    }

    /**
     * Set filter key, after this filter is not called.
     *
     * @param key key - case insensitive
     */
    public void setKey( final String key ) {
        final WebElement list = openList();
        final List<WebElement> items = list.findElements( By.tagName( "a" ) );
        for ( WebElement element : items ) {
            if ( element.getText().equalsIgnoreCase( key ) ) {
                element.click();
                return;
            }
        }
    }

    /**
     * Set filter key, after this filter is not called.
     *
     * @param index - index starts at 1
     */
    public void setKey( final int index ) {
        final WebElement list = openList();
        list.findElements( By.tagName( "a" ) ).get( index - 1 ).click();
    }

    /**
     * Clears quickFilter value and filters
     */
    public void clear() {
        set( "" );
    }

    /**
     * Pushs filter button
     */
    public void filter() {
        zkDriver.waitForProcessing();
        final WebElement image = webElement.findElement(By.className("datalite-listbox-qfiltr-image"));
        image.click();

        // filter click does not throw processing immediately
        zkDriver.sleep(100);
        zkDriver.waitForProcessing();
    }

    /**
     * Opens list of keys
     * @return List of keys
     */
    public WebElement openList() {
        final WebElement image = webElement.findElement( By.className( "datalite-listbox-qfiltr-open" ) );
        image.click();
        final WebElement list = ZkElement.findElement( By.className( "datalite-listbox-qfiltr-popup" ) );
        while ( list.getAttribute( "style" ).contains( "display: none" ) ) {
            image.click();
        }
        return list;
    }
}
