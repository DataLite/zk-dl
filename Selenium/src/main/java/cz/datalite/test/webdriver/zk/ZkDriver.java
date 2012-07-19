package cz.datalite.test.webdriver.zk;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.base.Function;
import cz.datalite.helpers.StringHelper;
import cz.datalite.webdriver.VisibilityOfElementLocated;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiri Bubnik
 */
public class ZkDriver implements WebDriver, Wait<WebDriver> {

    /** The webDriver instance. */
    protected WebDriver webDriver;
    /** Wait for instance. */
    protected Wait<WebDriver> webDriverWait;
    /** ZK ID prefix. */
    protected String idPrefix = "dtl";
    // Properties
    /** The server prefix (to construct URL). */
    protected String serverPrefix = "http://localhost:8787";
    /** Username for login screen. */
    private String username = "admin";
    /** Password for login screen. */
    private String password = "";
    /** Ajax wait timeout. */
    private int timeout = 20;
    /** Do full test of each component (slow). */
    private boolean fullTest = false;
    private String driver = "firefox";

    public ZkDriver() {
        loadProperties();

        if ( "htmlunit".equalsIgnoreCase( driver ) ) {
            initHtmlUnitDriver();
            initWait( timeout );
        } else if ( "ie".equalsIgnoreCase( driver ) ) {
            initIEDriver();
            initWait( timeout );
        } else {
            initFirefoxDriver();
            initWait( timeout );
        }
    }

    /**
     * Returns true for full tests.
     *
     * @return true if the test should be full (and thus can be slow)
     */
    public boolean isFullTest() {
        return fullTest;
    }

    /**
     * Setup properties in order:
     * <ul>
     *     <li>Default properties (http://localhost:8787, admin/)</li>
     *     <li>Property file zk-webdriver.properties</li>
     *     <li>Enviroment variables (for build server)</li>
     * </ul>
     */
    protected void loadProperties() {
        // load property file
        final InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream( "zk-webdriver.properties" );
        if ( file != null ) {
            final Properties properties = new Properties();

            try {
                properties.load( file );
            } catch ( IOException ex ) {
                Logger.getLogger( ZkDriver.class.getName() ).log( Level.SEVERE, null, ex );
                return;
            }

            serverPrefix = properties.getProperty( "server", serverPrefix );
            username = properties.getProperty( "username", username );
            password = properties.getProperty( "password", password );
            driver = properties.getProperty( "driver", driver );

            if ( properties.containsKey( "fullTest" ) ) {
                fullTest = "true".equalsIgnoreCase( properties.getProperty( "fullTest" ) );
            }

            if ( properties.containsKey( "timeout" ) ) {
                timeout = Integer.valueOf( properties.getProperty( "timeout" ) );
            }

        }

        // environment variables overwrites properties - usefull for build server
        final Map<String, String> env = System.getenv();
        if ( env.containsKey( "zk.webdriver.server" ) ) {
            serverPrefix = env.get( "zk.webdriver.server" );
        }
        if ( env.containsKey( "zk.webdriver.username" ) ) {
            username = env.get( "zk.webdriver.username" );
        }
        if ( env.containsKey( "zk.webdriver.password" ) ) {
            password = env.get( "zk.webdriver.password" );
        }
        if ( env.containsKey( "zk.webdriver.fullTest" ) ) {
            fullTest = "true".equalsIgnoreCase( env.get( "zk.webdriver.fullTest" ) );
        }
        if ( env.containsKey( "zk.webdriver.timeout" ) ) {
            timeout = Integer.valueOf( env.get( "zk.webdriver.timeout" ) );
        }
        if ( env.containsKey( "zk.webdriver.driver" ) ) {
            driver = env.get( "zk.webdriver.driver" );
        }

    }

    /**
     * Setup HtmlUnit driver with FIREFOX_3 compatibility and javascript enabled (mandatory for ZK functionality).
     */
    protected void initHtmlUnitDriver() {
        final HtmlUnitDriver htmlDriver = new HtmlUnitDriver( BrowserVersion.FIREFOX_3 );
        htmlDriver.setJavascriptEnabled( true );
        webDriver = htmlDriver;
    }

    /**
     * Setup IE driver (for debugging purposes).
     */
    protected void initIEDriver() {
        webDriver = new InternetExplorerDriver();
    }

    /**
     * Setup firefox driver (for debugging purposes).
     */
    protected void initFirefoxDriver() {
        webDriver = new FirefoxDriver();
    }

    /**
     * Close the driver and all resources (e.g. firefox window).
     *
     * Before window is closed, it checks for error window, copy out error message
     * and rethrow as java error (thus make test fail).
     */
    public void close() {
        try {
            waitForProcessing();
            final WindowElement error = findWindow( "winError", false );
            error.findButton( "showButton" ).click();
            waitForProcessing();
            sleep( 1000 );

            final WebElement element = error.findElement( By.id( getIdPrefix() + "_popis" ) );

            final StringBuilder errorText = new StringBuilder( "Error screen on execution end:\n" );

            for ( WebElement popis : element.findElements( By.tagName( "span" ) ) ) {
                final String text = popis.getText();
                if ( !StringHelper.isNull( text ) ) {
                    errorText.append( text );
                    errorText.append( "\n" );
                }
            }


            throw new IllegalStateException( errorText.toString() );
        } catch ( NoSuchElementException e ) {
            // ok, no error
        } finally {
            try {
                webDriver.close();
            } catch ( Exception ex ) {
                // html unit doesn't close correctly, igonre it
            }
        }


    }

    /**
     * Init waiting capablity of driver.
     *
     * @param timeout timeout in seconds.
     */
    protected void initWait( final int timeout ) {
        webDriverWait = new WebDriverWait( webDriver, timeout );
    }

    /**
     * Lookup window component by ZK page id and window id (wait for AJAX to finish before loading).<br/>
     * <br/>
     * In ZUL page use:
     * &lt;?page title="Page title" id="myModulePage"?&gt;<br/>
     * &lt;window id="myModuleWindow"&gt;
     *
     * @param pageId ZUL Page ID
     * @param windowId ZUL Window ID
     * @param wait wait for the page to load.
     *
     * @return the window
     *
     * @throws NoSuchElementException If no matching elements are found.
     */
    public WindowElement findWindow( final String windowId ) {
        return findWindow( windowId, true );
    }

    /**
     * Lookup window component by ZK page id and window id.<br/>
     * <br/>
     * In ZUL page use:
     * &lt;?page title="Page title" id="myModulePage"?&gt;<br/>
     * &lt;window id="myModuleWindow"&gt;
     *
     * @param pageId ZUL Page ID
     * @param windowId ZUL Window ID
     * @param wait wait for the page to load.
     *
     * @return the window
     *
     * @throws NoSuchElementException If no matching elements are found.
     */
    public WindowElement findWindow( final String windowId, final boolean wait ) {
        final String fullId = getIdPrefix() + "_" + windowId;
        if ( wait ) {
            webDriverWait.until( new VisibilityOfElementLocated( By.id( fullId ) ) );
        }

        return new WindowElement( this, null, findElement( By.id( fullId ) ), windowId );
    }

    /**
     * Return the main prefix of ZK component id.
     *
     * Client(html) ID is structured as prefix + id, where prefix is defined in zk.xml configuration file:<br/>
     * &lt;id-to-uuid-prefix&gt;dtl_${page}&lt;/id-to-uuid-prefix&gt;
     *
     * @return the prefix.
     */
    public String getIdPrefix() {
        return idPrefix;
    }

    /**
     * Returns the server prefix (according to properties settings).
     *
     * @return the server prefix 
     */
    public String getServerPrefix() {
        if ( serverPrefix.endsWith( "/" ) ) {
            return serverPrefix.substring( 0, serverPrefix.length() - 2 );
        } else {
            return serverPrefix;
        }
    }

    /**
     * Load a new web page in the current browser window. It use the Navigate method, for further information see WebDriver#get description.<br/>
     * <br/>
     * This method adds two features:<br/>
     * Address prefix: if the URL doesn't start with "http://", it is automatically prefixed with getServerPrefix().<br/>
     * Autologin: if the URL loads to login window, it fills login information with properties settings.
     *
     * @param url the URL to go to.
     *
     * @see WebDriver#get(String)
     */
    public void get( final String url ) {
        if ( url.startsWith( "http://" ) ) {
            webDriver.get( url );
        } else {
            webDriver.get( getServerPrefix() + url );
        }

        try {
            final WindowElement window = findWindow( "login", false );
            window.findTextbox( "username" ).sendKeys( username );
            window.findTextbox( "password" ).sendKeys( password );
            window.findElement( By.id( "dtlLoginScreenSubmit" ) ).click();
        } catch ( NoSuchElementException e ) {
            // Ok, login window not found or bad formated
        }
    }

    /**
     * Execute javascript in the context of the currently selected frame or
     * window. This means that "document" will refer to the current document.
     * If the script has a return value, then the following steps will be taken:
     *
     * <ul> <li>For an HTML element, this method returns a WebElement</li>
     * <li>For a number, a Long is returned</li>
     * <li>For a boolean, a Boolean is returned</li>
     * <li>For all other cases, a String is returned.</li>
     * <li>For an array, return a List&lt;Object&gt; with each object
     * following the rules above.  We support nested lists.</li>
     * <li>Unless the value is null or there is no return value,
     * in which null is returned</li> </ul>
     *
     * <p>Arguments must be a number, a boolean, a String, WebElement,
     * or a List of any combination of the above. An exception will be
     * thrown if the arguments do not meet these criteria. The arguments
     * will be made available to the javascript via the "arguments" magic
     * variable, as if the function were called via "Function.apply"
     *
     * @param script The javascript to execute
     * @param args The arguments to the script. May be empty
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     */
    public Object executeScript( final String script ) {
        return (( JavascriptExecutor ) webDriver).executeScript( script );
    }

    /**
     * Wait for ZK AU requests to finish.<br/>
     * <br/>
     * Evaluate javascript: zAu.processing() and waits unitl it returns false or null.
     */
    public void waitForProcessing() {
        webDriverWait.until( new ExpectedCondition<Boolean>() {

            public Boolean apply( final WebDriver f ) {
                //if ((Boolean) executeScript("if (zAu.processing()) { return true; } else { return false; }")) {
                final Object value = executeScript( "return zk.processing;" );
                if ( value == null || ( Boolean ) value ) {
                    return false;
                } else {
                    return true;
                }
            }
        } );
    }

    /**
     * Sleep for amount of time, usefull for some javascript operations without ZK Processing.
     * Use carefully, because it may cause unpredictable behaviour, wait for element or waitForProcessing()
     * is usually more appropriate.
     * 
     * @param miliseconds time for sleep in miliseconds
     */
    public void sleep( final int miliseconds ) {
        try {
            Thread.sleep( miliseconds );
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException( e );
        }
    }

    // method delegation for webdriver
    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    public void quit() {
        webDriver.quit();
    }

    public Navigation navigate() {
        return webDriver.navigate();
    }

    public Options manage() {
        return webDriver.manage();
    }

    public Set<String> getWindowHandles() {
        return webDriver.getWindowHandles();
    }

    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    public String getTitle() {
        return webDriver.getTitle();
    }

    public String getPageSource() {
        return webDriver.getPageSource();
    }

    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    public List<WebElement> findElements( final By by ) {
        return webDriver.findElements( by );
    }

    public WebElement findElement( final By by ) {
        return webDriver.findElement( by );
    }

    // method delegation for webDriverWait
    public <T> T until( final Function<? super WebDriver,T> isTrue ) {
        return webDriverWait.until( isTrue );
    }
}
