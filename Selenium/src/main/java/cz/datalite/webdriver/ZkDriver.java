package cz.datalite.webdriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import cz.datalite.webdriver.components.ZkElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>Selenium web driver offers only few methods how to work with page. This
 * class coinains it and extends so there are many other possibilities how 
 * to work with page.</p>
 * 
 * <p>There are enhancements written exactly for ZK framework pages so these
 * methods should be fully compatible with this ajax framework. Also there
 * are methods which programmers may find very useful. There are implemented
 * wait cycles which waits til page is loaded or component is shown. There
 * is loaded configuration and all properties are accessible via getters.</p>
 *
 * <p>This framework is dependent on DataLite application architecture and
 * its libraries beacause many rules are written into source-code and demands
 * strictly on application architecture. Especially ZkInfrastructure InitListener
 * as part of DLComponents which handles framework's ajax requests.</p>
 *
 * <p>Configuration is loaded from zk-webdriver.properties which is located
 * in project. There can be defined properties which can be overwritten
 * by enviroment properties. This cascade loading can be used on many
 * type of servers like build, testing or application as well as developing,
 * testing or production to set different configuration using enviroment
 * properties.</p>
 *
 * <h3>Acceptable properties are:</h3>
 * <ul>
 * <li><strong>driver</strong> - <em>Defines which core will be used. Options: firefox,
 * ie, chrome, htmlunit</em></li>
 * <li><strong>serverUrl</strong> - <em>Location of application, url location,
 * eg. http://localhost:8080/MyProject</em></li>
 * <li><strong>username</strong> - <em>Username which is filled into login page.</em></li>
 * <li><strong>password</strong> - <em>Password which is filled into login page</em></li>
 * <li><strong>timeout</strong> - <em>Each request to the server can have time limit.
 * Usually responses are ~ms but webdriver is pretty slow so one request
 * ussualy takes one second. Timeout is in seconds.</em></li>
 * <li><strong>fullTest</strong> - <em>Full test contains full test of listboxes
 * - sorting and filtering. This is slow process so there is
 * recommended to set up it only on testing servers.</em></li>
 * <li><strong>screenOnError</strong> - <em>When this framework detects ZK error it can take
 * a screenshot. This parameter turns it on, it is off in default.
 * Values are true and false.</em></li>
 * <li><strong>screenshotDir</strong> - <em>Taken screenshots have to be stored. There is
 * defined location. It can be absolute or relative from the applications
 * root. Screenshots are named according to actual
 * test class and method, should be store into build's directory because
 * there are no timestamps.</em></li>
 * <li><strong>screenshotUrl</strong> - <em>Taken screenshots can generate url and append it
 * into exceptions to be visible in testing server outputs'. This url
 * is assembled from this part and file name.</em></li>
 * </ul>
 *
 * @author Karel Cemus
 * @since version 3, 9.9.2010
 */
public class ZkDriver {

    protected static final Logger LOGGER = LoggerFactory.getLogger( "cz.datalite.selenium" );
    /** The webDriver instance. */
    protected WebDriver webDriver;
    /** Wait for instance. */
    protected Wait<WebDriver> webDriverWait;
    /** ZK ID prefix. */
    protected String idPrefix;
    // Properties
    /** The server prefix (to construct URL). */
    protected String serverUrl;
    /** Username for login screen. */
    protected String username;
    /** Password for login screen. */
    protected String password;
    /** Ajax wait timeout. */
    protected int timeout;
    /** Do full test of each component (slow). */
    protected boolean fullTest;
    /** driver name */
    protected String driver;
    /** dir for screens */
    protected String screenshotDir;
    /** url for screens accessing */
    protected String screenshotUrl;
    /** defines if take screenshot */
    protected boolean screenOnError;

    /**
     * Key for property map. This also serves as full
     * list of acception properties.
     */
    private static enum Property {

        DRIVER( "driver", "zk.webdriver.driver", "firefox" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.driver = properties.get( Property.DRIVER );
                if ( String.CASE_INSENSITIVE_ORDER.compare( "ie", driver.driver ) == 0 ) {
                    driver.webDriver = initIEDriver();
                } else if ( String.CASE_INSENSITIVE_ORDER.compare( "chrome", driver.driver ) == 0 ) {
                    driver.webDriver = initChromeDriver();
                } else if ( String.CASE_INSENSITIVE_ORDER.compare( "htmlunit", driver.driver ) == 0 ) {
                    driver.webDriver = initHTMLUnitDriver();
                } else {
                    driver.webDriver = initFirefoxDriver();
                }
            }
        },
        SERVER_URL( "server", "zk.webdriver.server", "http://localhost:8787" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.serverUrl = properties.get( Property.SERVER_URL );
            }
        },
        USERNAME( "username", "zk.webdriver.username", "admin" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.username = properties.get( Property.USERNAME );
            }
        },
        PASSWORD( "password", "zk.webdriver.password", "" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.password = properties.get( Property.PASSWORD );
            }
        },
        TIMEOUT( "timeout", "zk.webdriver.timeout", "20" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.timeout = Integer.parseInt( properties.get( Property.TIMEOUT ) );
            }
        },
        FULL_TEST( "fullTest", "zk.webdriver.fullTest", "false" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.fullTest = Boolean.valueOf( properties.get( Property.FULL_TEST ) );
            }
        },
        SCREENSHOT_DIR( "screenshotDir", "zk.webdriver.screenshotDir", "." ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.screenshotDir = properties.get( Property.SCREENSHOT_DIR );
            }
        },
        SCREENSHOT_URL( "screenshotUrl", "zk.webdriver.screenshotUrl", "" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.screenshotUrl = properties.get( Property.SCREENSHOT_URL );
            }
        },
        SCREEN_ON_ERROR( "screenOnError", "zk.webdriver.screenOnError", "false" ) {

            @Override
            public void set( final ZkDriver driver, final Map<Property, String> properties ) {
                driver.screenOnError = Boolean.valueOf( properties.get( Property.SCREEN_ON_ERROR ) );
            }
        };
        /** key in property file */
        protected final String propertyKey;
        /** key in environment */
        protected final String envKey;
        /** default value */
        protected final String defaultValue;

        private Property( final String propertyKey, final String envKey, final String defaultValue ) {
            this.propertyKey = propertyKey;
            this.envKey = envKey;
            this.defaultValue = defaultValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getEnvKey() {
            return envKey;
        }

        public String getPropertyKey() {
            return propertyKey;
        }

        public abstract void set( final ZkDriver driver, final Map<Property, String> properties );
    }

    /**
     * Loads property ( configuration ) file
     * @return loaded properties
     */
    private static Properties loadPropertyFile() {
        // load property file
        final InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream( "zk-webdriver.properties" );
        if ( file == null ) {
            throw new IllegalStateException( "Property file wasn't found." );
        } else {
            final Properties properties = new Properties();
            try {
                properties.load( file );
                return properties;
            } catch ( IOException ex ) {
                LOGGER.error("Property file couldn't be loaded", ex );
                throw new IllegalStateException( "Property file wasn't loaded." );
            }
        }
    }

    /**
     * Loads one property from file and from environment and the one which
     * higher priority returns. Also accepts default values which are used
     * when the property is defined nowhere.
     * @param properties Object with loaded properties
     * @param env environment properties
     * @param key key of property in property file
     * @param envKey key of property in environment
     * @param defaultValue default value
     * @return value with highest priority
     */
    private static String loadProperty( final Properties properties, final Map<String, String> env, final String key, final String envKey, final String defaultValue ) {
        // environment variables overwrites properties - usefull for build server
        String value = properties.getProperty( key, defaultValue );
        if ( env.containsKey( envKey ) ) {
            value = env.get( envKey );
        }
        return value;
    }

    /**
     * Loads properties from file and environment and transforms them into map
     * @return loaded properties
     */
    private static Map<Property, String> loadProperties() {
        final Map<Property, String> propertyMap = new EnumMap<Property, String>( Property.class );
        final Properties propertyFile = loadPropertyFile();
        final Map<String, String> env = System.getenv();

        for ( Property property : Property.values() ) {
            propertyMap.put( property, loadProperty( propertyFile, env, property.getPropertyKey(), property.getEnvKey(), property.getDefaultValue() ) );
        }

        return propertyMap;
    }

    private static WebDriver initIEDriver() {
        return new InternetExplorerDriver();
    }

    private static WebDriver initChromeDriver() {
        return new ChromeDriver();
    }

    private static WebDriver initHTMLUnitDriver() {
        final HtmlUnitDriver htmlDriver = new HtmlUnitDriver( BrowserVersion.getDefault() );
        htmlDriver.setJavascriptEnabled( true );
        return htmlDriver;
    }

    private static WebDriver initFirefoxDriver() {
        return new FirefoxDriver();
    }

    public static ZkDriver init() {
        final Map<Property, String> properties = loadProperties();
        final ZkDriver zkDriver = new ZkDriver( properties );
        ZkElement.setZkDriver( zkDriver );
        return zkDriver;
    }

    protected ZkDriver( final Map<Property, String> properties ) {
        for ( Property property : Property.values() ) {
            property.set( this, properties );
        }
        webDriverWait = new WebDriverWait( webDriver, timeout );
    }

    public boolean isFullTest() {
        return fullTest;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public String getPassword() {
        return password;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getUsername() {
        return username;
    }

    public String getScreenshotDir() {
        return screenshotDir;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public boolean isScreenOnError() {
        return screenOnError;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public Wait<WebDriver> getWebDriverWait() {
        return webDriverWait;
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
    public Object executeScript( final String script, final Object... args ) {
        return (( JavascriptExecutor ) webDriver).executeScript( script, args );
    }

    /**
     * Wait for ZK AU requests to finish.<br/>
     * <br/>
     * Evaluate javascript: zAu.processing() and waits unitl it returns false or null.
     */
    public void waitForProcessing() {
        webDriverWait.until( new VisibilityOfElementLocated( By.tagName( "body" ) ) );
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

    public void waitForElement( final String id ) {
        waitForElement(By.id( id ));
    }

    public void waitForElement( final By by ) {
        waitForProcessing();
        webDriverWait.until( new VisibilityOfElementLocated( by ) );
    }

    public void waitForElement( final WebElement parent, final By by ) {
        waitForProcessing();
        webDriverWait.until( new VisibilityOfElementLocated( parent, by ) );
    }

    public void waitForElementVisible( final By by ) {
        waitForProcessing();
        webDriverWait.until( new VisibilityOfElementLocated( by ) );
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

    /**
     * Close the driver and all resources (e.g. firefox window).
     *
     * Before window is closed, it checks for error window, copy out error message
     * and rethrow as java error (thus make test fail).
     */
    public void close() {
        try {
            webDriver.close();
        } catch ( Exception ex ) {
            // html unit doesn't close correctly, ignore it
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
            webDriver.get( getServerUrl() + url );
        }
        webDriverWait.until( new VisibilityOfElementLocated( By.tagName( "body" ) ) );
        ZkElement.tryLogin();
    }

    /**
     * This method sends command to the server with defined value and
     * waits for the response. This is used to translate ids to uuids,
     * for completing listboxes etc. On server side has to be InitListener
     * from Module ZKDLComponents which handles these requests.
     * @param command command which is known on server side
     * @param value command parameter
     * @return server's response
     */
    public String sendCommand( final String command, final String value ) {
        executeScript( "zAu.send(new zk.Event(null, 'onDataliteTestService', '" + command + ":" + value + "', 10))" );
        waitForProcessing();
        return webDriver.findElement( By.className( "selenium-response" ) ).getText();
    }

}
