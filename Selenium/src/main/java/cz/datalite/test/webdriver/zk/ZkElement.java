package cz.datalite.test.webdriver.zk;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Top of ZK test component class hierarchy.
 *
 * Typical usege is with a subclass such as WindowElement or ListboxElement,
 * however simple components like Label may return directly this type.
 *
 * Connection to WebDriver WebElements is via composition, i.e. mandatory
 * constructor is WebElement instance - component conterpart in html (javascript widget).
 *
 * @author Jiri Bubnik
 */
public class ZkElement implements WebElement {

    /** The Web driver for a test. This is used to call support methods etc. */
    protected final ZkDriver zkDriver;
    /** The web element instance (aka client component counterpart). */
    protected WebElement webElement;
    /** Tree structure of components (emulate sever object tree). */
    protected ZkElement parent;
    /** Id of the component on server (ZK ID). */
    protected String zkId;

    /**
     * Create new element with it's client widget countepart represented by webElement.
     *
     * @param zkDriver driver
     * @param parent parent (component tree structure)
     * @param webElement the client element.
     */
    public ZkElement( final ZkDriver zkDriver, final ZkElement parent, final WebElement webElement ) {
        this.zkDriver = zkDriver;
        this.parent = parent;
        this.webElement = webElement;
    }

    /**
     * Create new element with it's client widget countepart represented by webElement.
     *
     * @param zkDriver driver
     * @param parent parent (component tree structure)
     * @param zkId id of the ZK part
     */
    public ZkElement( final ZkDriver zkDriver, final ZkElement parent, final String zkId ) {
        this.zkDriver = zkDriver;
        this.parent = parent;
        this.zkId = zkId;
    }

    /**
     * ID of the component on serveru (ZK ID).
     *
     * Client(html) ID is structured as prefix + id, where prefix is defined in zk.xml configuration file:<br/>
     * &lt;id-to-uuid-prefix&gt;dtl_${page}&lt;/id-to-uuid-prefix&gt;
     *
     * @return ID or null if not specified (component was found by other parameters).
     */
    public String getZkId() {
        return zkId;
    }

    /**
     * Returns the driver assiciated with this element.
     *
     * @return the driver
     */
    public ZkDriver getZkDriver() {
        return zkDriver;
    }

    /**
     * Returns parent component (component tree structure).
     * 
     * @return component parent or null if tree root.
     */
    public ZkElement getParent() {
        return parent;
    }

    /**
     * Traverse component tree up and finds parent window.
     *
     * @return parent window
     * @throws IllegalStateException if not found
     */
    public WindowElement getWindow() {
        if ( parent == null ) {
            throw new IllegalStateException( "Parent window component not found" );
        } else {
            return parent.getWindow(); // overwritten in WindowElement
        }
    }

    /**
     * To provide unique client(html) ID, prefix server component ZK ID with custom prefix.
     *
     * Client(html) ID is structured as prefix + id, where prefix is defined in zk.xml configuration file:<br/>
     * &lt;id-to-uuid-prefix&gt;dtl&lt;/id-to-uuid-prefix&gt;
     *
     * @return the prefix (including trailing _: e.g. "dtl_")
     */
    public String getComponentIdPrefix() {
        if ( getParent() == null ) {
            return zkDriver.getIdPrefix() + "_";
        } else {
            return getParent().getComponentIdPrefix();
        }
    }

    public WebElement findElement( final By by, final boolean wait ) {
        if ( wait ) {
            getZkDriver().until( ExpectedConditions.visibilityOfElementLocated(by));
        }

        return findElement(by);
    }

    // Method delegation methods to webElement instance
    @Override
    public int hashCode() {
        return webElement.hashCode();
    }

    @Override
    public boolean equals( final Object obj ) {
        return webElement.equals( obj );
    }

    public void submit() {
        webElement.submit();
    }

    public void sendKeys( final CharSequence... keysToSend ) {
        webElement.sendKeys( keysToSend );
    }

    public boolean isSelected() {
        return webElement.isSelected();
    }

    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    public String getText() {
        return webElement.getText();
    }

    public String getTagName() {
        return webElement.getTagName();
    }

    public String getAttribute( final String name ) {
        return webElement.getAttribute( name );
    }

    public List<WebElement> findElements( final By by ) {
        return webElement.findElements( by );
    }

    public WebElement findElement( final By by ) {
        return webElement.findElement( by );
    }

    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    public Point getLocation() {
        return webElement.getLocation();
    }

    public Dimension getSize() {
        return webElement.getSize();
    }

    public String getCssValue(String propertyName) {
        return webElement.getCssValue(propertyName);
    }

    public void click() {
        webElement.click();
    }

    public void clear() {
        webElement.clear();
    }
}
