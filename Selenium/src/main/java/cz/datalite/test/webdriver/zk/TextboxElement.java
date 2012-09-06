package cz.datalite.test.webdriver.zk;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * ZK Element textbox.
 *
 * @author Jiri Bubnik
 */
public class TextboxElement extends ZkElement {

    public static final By XPATH_SUBELEMENTS = By.xpath( ".//textarea|.//input[@type='text'|@type='password']" );

    public TextboxElement( final ZkDriver zkDriver, final ZkElement parent, final String zkId ) {
        super( zkDriver, parent, zkId );
        webElement = parent.findElement( By.id( getComponentIdPrefix() + zkId ) );
    }

    public TextboxElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }

    /**
     * Clear current text and write new text.
     *
     * @param text the text to write
     *
     * @return this object for dot concatenate syntax suggar
     */
    public TextboxElement writeText( final String text ) {
        clear();
        sendKeys( text );

        return this;
    }

    /**
     * Finds next element.
     *
     * @return return next element of type textbox (input).
     */
    public TextboxElement next() {
        final List<WebElement> elements = getParent().findElements( XPATH_SUBELEMENTS );

        final int index = elements.indexOf( this );

        if ( index == -1 ) {
            throw new NoSuchElementException( "This textbox '" + getText() + "'is not found in parent children: can't use the algorithm to find next element." );
        }

        if ( index == elements.size() - 1 ) {
            throw new NoSuchElementException( "This is the last element of type 'input' or 'textarea' under parent element." );
        }

        return new TextboxElement( getZkDriver(), getParent(), elements.get( index + 1 ) );
    }
}
