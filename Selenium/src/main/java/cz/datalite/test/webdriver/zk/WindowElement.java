package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.WebElement;

/**
 * Window component.
 *
 * @author Jiri Bubnik
 */
public class WindowElement extends ZkElement {

    public WindowElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement, final String zkId ) {
        super( zkDriver, parent, zkId );

        this.webElement = webElement;
    }

    public WindowElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        super( zkDriver, parent, webElement );
    }

    // overwritten from ZkElement to allow parent traversal and find parent window.
    @Override
    public WindowElement getWindow() {
        return this;
    }

    /**
     * Find textbox component by it's id.
     *
     * @param id server id of the textbox.
     *
     * @return the textbox (or throws NoSuchElementException if not found)
     *
     * @throws NoSuchElementException If no matching elements are found.
     */
    public TextboxElement findTextbox( final String id ) {
        return new TextboxElement( getZkDriver(), this, id );
    }

    /**
     * Find child texbox by index.
     * 
     * @param index index of child textbox
     * @return child textbox
     */
    public TextboxElement findTextbox( final int index ) {
        return new TextboxElement( getZkDriver(), this, findElement( TextboxElement.XPATH_SUBELEMENTS ) );
    }

    /**
     * Find button component by it's id.
     *
     * @param id server id of the button.
     *
     * @return the button (or throws NoSuchElementException if not found)
     *
     * @throws java.util.NoSuchElementException If no matching elements are found.
     */
    public ButtonElement findButton( final String id ) {
        return new ButtonElement( getZkDriver(), this, id );
    }

    public ListboxElement findListbox( final String id ) {
        return new ListboxElement( getZkDriver(), this, id );
    }
}
