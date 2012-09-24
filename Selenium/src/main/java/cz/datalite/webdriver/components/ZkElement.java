package cz.datalite.webdriver.components;

import cz.datalite.helpers.StringHelper;
import cz.datalite.webdriver.ElementFactory;
import cz.datalite.webdriver.SeleniumUnitTest;
import cz.datalite.webdriver.ZkDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cz.datalite.webdriver.ZkComponents.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is base element for all ZK elements on the page. There are
 * implemented standard function for find elements as well as static
 * functions which handles login page, errors, takes screenshot etc.</p>
 *
 * @author Karel Cemus
 */
public class ZkElement {

    protected static final Logger LOGGER = LoggerFactory.getLogger( "cz.datalite.selenium" );
    /** The Web driver for a test. This is used to call support methods etc. */
    protected static ZkDriver zkDriver;
    /** The web element instance (aka client component counterpart). */
    protected WebElement webElement;
    /** Tree structure of components (emulate sever object tree). */
    protected ZkElement parent;

    /**
     * Returns the driver assiciated with this element.
     *
     * @return the driver
     */
    public static ZkDriver getZkDriver() {
        return zkDriver;
    }

    public static void setZkDriver( final ZkDriver driver ) {
        zkDriver = driver;
    }

    /**
     * <p>Waits until component specified by By is not shown or timeout exceed.</p>
     * @param by How to find component
     */
    protected static WebElement waitUntilShown(final By by) {
        return zkDriver.getWebDriverWait().until( ExpectedConditions.visibilityOfElementLocated(by) );
    }

    /**
     * <p>Finds element and returns it without wrapping with ZkElement.
     * This element has no parent so it is looked up in whole page
     * and it is not waiting til element is shown.</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException}
     * is thrown.</p>
     *
     * @param by how to find it
     * @return unwrapped found element
     */
    public static WebElement findElement( final By by ) {
        return findElement( by, null );
    }

    /**
     * <p>Finds element and returns it without wrapping with ZkElement.
     * It is not waiting til element is shown.</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException}
     * is thrown.</p>
     * @param by how to find it
     * @param contextElement content element - in this subtree will be searched
     * @return found component.
     */
    public static WebElement findElement( final By by, final ZkElement contextElement ) {
        return findElement( by, contextElement, false );
    }

    /**
     * <p>Finds element and returns it without wrapping with ZkElement.
     * This element has parent and it is not waiting til element is shown.
     * Some types of searching requires new looking query after first one and
     * the second criteria is defined as third parameter</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException}
     * is thrown.</p>
     * @param by how to find parent
     * @param contextElement parent of first component
     * @param childBy how to find child
     * @return found component.
     */
    public static WebElement findElement( final By by, final ZkElement contextElement, final By childBy ) {
        return findElement( by, contextElement, false, childBy );
    }

    /**
     * <p>Finds element with defined parent and criteria and specifies if the
     * test has to wait til the component is shown or not. This waiting can
     * be interrupted by timeout exceeding.</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException}
     * is thrown.</p>
     * @param by criterion
     * @param contextElement parent
     * @param wait wait til component is shown
     * @return found component.
     */
    public static WebElement findElement( final By by, final ZkElement contextElement, final boolean wait ) {
        return findElement( by, contextElement, wait, null );
    }

    /**
     * <p>Finds element with defined parent and criteria and specifies if the
     * test has to wait til the component is shown or not. This waiting can
     * be interrupted by timeout exceeding.Some types of searching requires
     * new looking query after first one and the second criteria is defined
     * as third parameter.</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException} is thrown.</p>
     *
     * @param by criterion
     * @param contextElement parent
     * @param wait wait til component is shown
     * @param childBy how to find child
     * @return found component.
     */
    public static WebElement findElement( final By by, final ZkElement contextElement, final boolean wait, final By childBy ) {
        if ( wait ) {
            waitUntilShown( by );
        }
        return find( by, contextElement, childBy );
    }

    /**
     * <p>Main searching function which finds unwrapped elements by
     * given criteria.</p>
     *
     * <p>If the component is not found then the
     * {@link org.openqa.selenium.NoSuchElementException} is thrown.</p>
     *
     * @param by criterion
     * @param contextElement parent
     * @param childBy criterion fow child if it is required.
     * @return the element
     */
    public static WebElement find( final By by, final ZkElement contextElement, final By childBy ) {
        WebElement element;
        if ( contextElement == null ) {
            element = zkDriver.getWebDriver().findElement( by );
        } else {
            element = contextElement.getWebElement().findElement( by );
        }

        if ( by instanceof cz.datalite.webdriver.By && childBy != null ) {
            final cz.datalite.webdriver.By dlBy = ( cz.datalite.webdriver.By ) by;
            if ( dlBy.isLookForChild() ) {
                element = element.findElement( childBy );
            }
        }
        return element;
    }

    /**
     * Finds all elements which meet given criteria
     * @param by criteria
     * @param contextElement parent of elements in DOM
     * @return list of found unwrapped elements
     */
    public static List<WebElement> findElements( final By by, final ZkElement contextElement ) {

        if ( contextElement == null ) {
            return zkDriver.getWebDriver().findElements( by );
        } else {
            return contextElement.getWebElement().findElements( by );
        }
    }

    /**
     * Returns first window on the page
     * @return first window
     */
    public static Window findWindow() {
        return findWindow( WINDOW.getBy() );
    }

    /**
     * Returns first window which meets requirements
     * @param by requirements
     * @return window element
     */
    public static Window findWindow( final By by ) {
        return findWindow( by, true );
    }

    /**
     * <p>Finds first windows on the page and has option
     * if to wait til window appears or not. It can
     * throw {@link org.openqa.selenium.NoSuchElementException}
     *
     * @param by criteria
     * @param wait if wait til component appears
     * @return found component
     */
    public static Window findWindow( final By by, final boolean wait ) {
        try {
            return new Window( null, findElement( by, null, wait ) );
        } catch ( Exception ex ) {
            throw new NoSuchElementException( "Window looked up by: '" + by.toString() + "' wasn't found." );
        }
    }

    /**
     * Takes screenshot of actual visible window in browser. It is useful
     * when the application throws and error to screen it and then add to 
     * report.
     * 
     * @return screenshot url
     */
    public static String takeScreenshot() {
        if ( zkDriver.getWebDriver() instanceof TakesScreenshot ) {
            try {
                final TakesScreenshot driver = ( TakesScreenshot ) zkDriver.getWebDriver();
                final File f = driver.getScreenshotAs( OutputType.FILE );
                final File screenshot = new File( zkDriver.getScreenshotDir(), SeleniumUnitTest.getActualTestName() + ".png" );
                FileUtils.copyFile( f, screenshot );
                LOGGER.debug( "Screenshot taken. File location is '{}'.", screenshot.getAbsolutePath() );
                return zkDriver.getScreenshotUrl() + screenshot.getName();
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        } else {
            return "";
        }
    }

    /**
     * Looks at the page and decides if the error is shown. In that case
     * the call stack is read and composed to report, screenshot is taken
     * and then is thrown exception.
     */
    public static void handleError() {
        try {
            zkDriver.waitForProcessing();
            final Window errorWin = findWindow( By.className( "error-window" ), false );
            errorWin.findButton( By.className( "error-window-show" ) ).click();
            zkDriver.waitForProcessing();
            zkDriver.sleep( 1000 );

            final WebElement element = (( ZkElement ) errorWin.find( By.className( "error-window-details" ) )).getWebElement();

            final StringBuilder errorText = new StringBuilder( "Test failed:\n" );

            if ( zkDriver.isScreenOnError() ) { // if it is enabled then take screenshot
                errorText.append( "\nScreenshot is available at " ).append( takeScreenshot() ).append( " \n\n" );
            }

            for ( WebElement popis : element.findElements( By.tagName( "span" ) ) ) {
                final String text = popis.getText();
                if ( !StringHelper.isNull( text ) ) {
                    errorText.append( text ).append( "\n" );
                }
            }
            throw new IllegalStateException( errorText.toString() );
        } catch ( NoSuchElementException e ) {
            // ok, no error
        }
    }

    /**
     * After login there is looked for error window which says that
     * login failed. In this case exception is thrown.
     */
    public static void handleLoginError() {
        try {
            zkDriver.waitForProcessing();
            findWindow( By.className( "login-error" ), false );
            zkDriver.waitForProcessing();
            final StringBuilder message = new StringBuilder( 200 );
            message.append( "Login failed." );
            if ( zkDriver.isScreenOnError() ) { // if it is enabled then take screenshot
                message.append( "\n\nScreenshot is available at " ).append( takeScreenshot() ).append( " .\n\n" );
            }
            throw new IllegalStateException( message.toString() );
        } catch ( NoSuchElementException e ) {
            // ok, no error
        }
    }

    /**
     * Tries to find login window and fill it according to configuration.
     * It is not found then nothing.
     */
    public static void tryLogin() {
        try {
            zkDriver.waitForProcessing();
            final Window window = findWindow( By.className( "login-window" ), false );
            final Textbox username = window.find( By.className( "login-username" ) );
            username.write( zkDriver.getUsername() );

            final Textbox password = window.find( By.className( "login-password" ) );
            password.write( zkDriver.getPassword() );

            window.findButton( By.className( "login-submit" ) ).click();

            handleLoginError();
        } catch ( NoSuchElementException e ) {
            // Ok, login window not found or bad formated
        }
    }

    /**
     * Sends special javascript event which should emulate doubleclick.
     * It is dependent on browser.
     * @param element target element
     */
    protected static void doubleClickOn( final WebElement element ) {
        new Actions(zkDriver.getWebDriver()).doubleClick(element).perform();
//        if ( zkDriver.getWebDriver() instanceof InternetExplorerDriver ) {//        This works for IE driver
//            zkDriver.executeScript( "arguments[0].fireEvent('ondblclick')", element );
//            zkDriver.waitForProcessing();
//        } else { //        For the FirefoxDriver and 'ChromeDriver':
//            zkDriver.executeScript(
//                    "var evt = document.createEvent('MouseEvents'); evt.initMouseEvent('dblclick',"
//                    + "true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,"
//                    + "null); "
//                    + "arguments[0].dispatchEvent(evt);", element );
//        }
    }

    /**
     * Create new element with it's client widget countepart represented by webElement.
     *
     * @param parent parent (component tree structure)
     * @param webElement the client element.
     */
    public ZkElement( final ZkElement parent, final WebElement webElement ) {
        this.parent = parent;
        this.webElement = webElement;
    }

    /**
     * Returns parent component (component tree structure).
     *
     * @return component parent or null if tree root.
     */
    public ZkElement getParent() {
        return parent;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    /**
     * Traverse component tree up and finds parent window.
     *
     * @return parent window
     * @throws IllegalStateException if not found
     */
    public Window getWindow() {
        if ( parent == null ) {
            throw new IllegalStateException( "Parent window component not found. This component has no parent." );
        } else {
            return parent.getWindow(); // overwritten in Window
        }
    }

    /**
     * Finds element with this parent and given criterion.
     * @param <T> expected type - is ignored, type is determined by css class.
     * If there was expected wrong class then there would be thrown
     * ClassCastException
     * @param by criterion
     * @return found element derived from ZkElement
     */
    @SuppressWarnings("unchecked")
    public <T extends ZkElement> T find( final By by ) {
        return ( T ) ElementFactory.create( findElement( by, this ), this );
    }

    /**
     * Finds all elements with this parent and given criterion.
     * @param <T> expected type - is ignored, type is determined by css class.
     * If there was expected wrong class then there would be thrown
     * ClassCastException
     * @param by criterion
     * @return found element derived from ZkElement
     */
    @SuppressWarnings("unchecked")
    public <T extends ZkElement> List<T> findAll( final By by ) {
        final List<WebElement> webElements = findElements( by, this );
        final List<T> elements = new ArrayList<T>( webElements.size() );

        for ( WebElement element : webElements ) {
            final T zkElement = ( T ) ElementFactory.create( element, this );
            elements.add( zkElement );
        }
        return elements;
    }

    public Button findButton( final By by ) {
        if ( by.toString().contains( "By.label" ) ) { // looking up by button label in form
            final String label = by.toString().substring( 10 );
            return new Button( this, findElement( By.xpath( ".//button[text()='" + label + "']" ), this ) );
        } else {
            return new Button( this, findElement( by, this, BUTTON.getBy() ) );
        }
    }

// ToDo Not implemented yet
//    public ToolbarButton findToolbarButton( String label ) {
//        WebElement labelElement = findElement( By.xpath(".//div[contains(@class, 'z-toolbarbutton')]/div/div[text()='" + label + "']/../.."), this);
//        return new ToolbarButton( this, labelElement );
//    }


    public Checkbox findCheckbox( final By by ) {
        return new Checkbox( this, findElement( by, this, CHECKBOX.getBy() ) );
    }

    public Datebox findDatebox() {
        return findDatebox( DATEBOX.getBy() );
    }

    public Datebox findDatebox( final By by ) {
        return new Datebox( this, findElement( by, this, DATEBOX.getBy() ) );
    }

    public Decimalbox findDecimalbox() {
        return findDecimalbox( DECIMALBOX.getBy() );
    }

    public Decimalbox findDecimalbox( final By by ) {
        return new Decimalbox( this, findElement( by, this, DECIMALBOX.getBy() ) );
    }

    public Doublebox findDoublebox() {
        return findDoublebox( DOUBLEBOX.getBy() );
    }

    public Doublebox findDoublebox( final By by ) {
        return new Doublebox( this, findElement( by, this, DOUBLEBOX.getBy() ) );
    }

    public Intbox findIntbox() {
        return findIntbox( INTBOX.getBy() );
    }

    public Intbox findIntbox( final By by ) {
        return new Intbox( this, findElement( by, this, INTBOX.getBy() ) );
    }

    public Grid findGrid() {
        return findGrid( GRID.getBy() );
    }

    public Grid findGrid( final By by ) {
        return new Grid( this, findElement( by, this, GRID.getBy() ) );
    }

    public Listbox findListbox() {
        final List<WebElement> listboxes = findElements( LISTBOX.getBy(), this );
        for ( WebElement element : listboxes ) {
            if ( element.isDisplayed() ) {
                return new Listbox( this, element );
            }
        }
        throw new NoSuchElementException( "Window doesn't contain any visible listbox." );
    }

    public Listbox findListbox( final By by ) {
        return new Listbox( this, findElement( by, this, LISTBOX.getBy() ) );
    }

    public Set<Listbox> findListboxes() {
        final List<WebElement> elements = findElements( LISTBOX.getBy(), this );
        final Set<Listbox> listboxes = new HashSet<Listbox>();
        for ( WebElement element : elements ) {
            if ( element.isDisplayed() ) {
                listboxes.add( new Listbox( this, element ) );
            }
        }
        return listboxes;
    }

// ToDo Not implemented yet    
//    public Tree findTree( final By by ) {
//        return new Tree( this, findElement( by, this, TREE.getBy() ) );
//    }

    public Lovbox findLovbox() {
        return findLovbox( LOVBOX.getBy() );
    }

    public Lovbox findLovbox( final By by ) {
        return new Lovbox( this, findElement( by, this, LOVBOX.getBy() ) );
    }

    public Textbox findTextbox() {
        return findTextbox( TEXTBOX.getBy() );
    }

    public Textbox findTextbox( final By by ) {
        return new Textbox( this, findElement( by, this, TEXTBOX.getBy() ) );
    }

    /**
     * Returns if the element is shown or not. Invisible elements don't
     * support some methods.
     * @return if the element is displayed
     */
    public boolean isVisible() {
        return webElement.isDisplayed();
    }

    @Override
    public int hashCode() {
        return webElement.hashCode();
    }

    @Override
    public boolean equals( final Object obj ) {
        return webElement.equals( obj );
    }
}
